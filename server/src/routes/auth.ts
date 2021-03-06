import bcrypt from "bcrypt";
import dotenv from "dotenv";
import express, { Request, Response, Router } from "express";
import jwt from "jsonwebtoken";
import passport from "passport";
import {
  Strategy as GoogleStrategy,
  VerifyCallback,
} from "passport-google-oauth20";
import { checkAuthentication } from "../utils/middlewares";
import {
  changePassword,
  createOauthUser,
  createUser,
  deleteRefreshToken,
  findOneRefreshToken,
  getOneOauthUser,
  getOneUser,
  saveRefreshToken,
  sendVerificationCode,
  updateUser,
  verifyCode,
} from "../controllers/UserController";
import ApiResponse from "../utils/ApiResponse";
import CustomError from "../utils/CustomError";
import { User } from "../entity/User";
import { VerifyCodeAction } from "src/utils/types";
dotenv.config();

const router = Router();
router.use(express.json());

const handleAuth = async (
  _accessToken: string,
  _refreshToken: string,
  profile: any,
  cb: VerifyCallback
) => {
  try {
    const { id, emails, displayName, photos } = profile;
    console.log(id, emails, displayName, photos, profile);
    let user = await getOneOauthUser(id);
    if (!user) {
      //Register
      await createOauthUser(
        id,
        displayName,
        emails && emails[0].value,
        photos && photos[0].value
      );
      user = await getOneOauthUser(id);
    }
    if (user) return cb(null, user);
    else throw new Error("There's something wrong");
  } catch (error) {
    return cb(error, undefined);
  }
};

router.use(passport.initialize());
passport.use(
  new GoogleStrategy(
    {
      clientID: process.env.GOOGLE_CLIENT_ID as string,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET as string,
      callbackURL: process.env.GOOGLE_CLIENT_SECRET as string,
    },
    handleAuth
  )
);

//Middlewares

//Routes
router.get("/", checkAuthentication, (req, res) => {
  const user = req && (req as any).user;
  if (user) {
    res.send(new ApiResponse(true, "", user));
  } else {
    res.send(new ApiResponse(true, "Not logged in!", null));
  }
});

router.get("/verify/code", async (req, res) => {
  try {
    const email = req.query.email as string;

    console.log("Prepare to send verify code")

    // const email = "adron0914@gmail.com";

    await sendVerificationCode(email);

    res.send(new ApiResponse(true, "", null));
  } catch (error) {
    console.log("Error: ", error.message)
    if (error instanceof CustomError) {
      res.send(new ApiResponse(false, error.message, null));
    } else {
      res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.post("/verify/code", async (req, res) => {
  try {
    const email = req.body.email;
    const code = req.body.code;
    const action = req.body.action as VerifyCodeAction;
    console.log("updating password: ", email, code, action)

    const result = await verifyCode(email, code);

    if(result){
      switch(action.actionTitle){
        case "reset_password": {
          const newPassword = action.actionValue as string
          const hashPW = bcrypt.hashSync(
            newPassword,
            parseInt(process.env.SALT_ROUNDS!)
          );
          console.log("New hash password: ", hashPW)
          changePassword(email, hashPW)
          break;
        }
      }
      res.send(new ApiResponse(true, "", null));
    } else {
      res.send(new ApiResponse(false, "Wrong verification code!", null));
    }

  } catch (error) {
    if (error instanceof CustomError) {
      res.send(new ApiResponse(false, error.message, null));
    } else {
      res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});


router.post("/register", async (req, res) => {
  try {
    const username = req.body.username;
    const email = req.body.email;
    const password = req.body.password;
    const avatar = req.body.avatar;
    const native = req.body.nativeLanguage;

    //Check user input
    if (!username || !email || !password || !native)
      throw new CustomError("Please provide the required information");

    //Create hash password
    const hashPW = bcrypt.hashSync(
      password,
      parseInt(process.env.SALT_ROUNDS!)
    );

    //Create new user
    await createUser(username, native, email, hashPW, avatar);

    return res.send(new ApiResponse(true, "", null));
  } catch (error) {
    if (error instanceof CustomError) {
      return res.send(new ApiResponse(false, error.message, null));
    } else {
      return res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.post("/login", async (req, res) => {
  // Authenticate User
  console.log("logging in", req.body.usernameOrEmail);
  try {
    //Check user input
    if (!req.body.usernameOrEmail)
      throw new CustomError("Please provide username or email!");
    if (!req.body.password) throw new CustomError("Please provide password");

    //Check if user is registered
    const user = await getOneUser(req.body.usernameOrEmail);
    console.log("user found", user);

    if (!user)
      throw new CustomError(
        "No user found with the given username or password!"
      );

    //Check password
    const result = await bcrypt.compare(req.body.password, user.password);
    if (!result) throw new CustomError("Invalid credentials!");

    //Create new tokens and save to database
    const accessToken = jwt.sign({ user }, process.env.ACCESS_TOKEN_SECRET!, {
      expiresIn: process.env.TOKEN_EXPIRATION,
    });
    const refreshToken = jwt.sign({ user }, process.env.REFRESH_TOKEN_SECRET!);
    await saveRefreshToken(user.id, refreshToken);

    //Send response
    return res.send(new ApiResponse(true, "", { accessToken, refreshToken }));
  } catch (error) {
    if (error instanceof CustomError) {
      return res.send(new ApiResponse(false, error.message, null));
    } else {
      return res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.delete("/logout", async (req, res) => {
  try {
    const refreshToken = req.body.refreshToken;

    await deleteRefreshToken(refreshToken);

    return res.send(new ApiResponse(true, "", null));
  } catch (error) {
    if (error instanceof CustomError) {
      return res.send(new ApiResponse(false, error.message, null));
    } else {
      return res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});


router.put("/user", checkAuthentication, async (req, res) => {
  try {
    const userId = (req as any).user.id;
    if (!userId) {
      throw new CustomError("Please login first!");
    }

    const newUser = await updateUser(
      userId,
      req.body.username,
      req.body.email,
      req.body.isPremium,
      req.body.avatar,
      req.body.dailyWordCount,
      req.body.notificationEnabled,
      req.body.nativeLanguageId,
      req.body.appLanguageId,
      req.body.dailyWordTopic,
      req.body.feedTopics
    );

    console.log("Update user successfully!", newUser);

    return res.send(new ApiResponse(true, "", newUser));
  } catch (error) {
    if (error instanceof CustomError) {
      return res.send(new ApiResponse(false, error.message, null));
    } else {
      return res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.post("/token", (req, res) => {
  try {
    const refreshToken = req.body.token;

    //Verify user input
    if (refreshToken == null) throw new CustomError("Unauthorized");

    //Check if the refresh token is stored in the database
    if (!findOneRefreshToken(refreshToken)) throw new CustomError("Forbidden");

    // Verify the refresh token
    jwt.verify(
      refreshToken,
      process.env.REFRESH_TOKEN_SECRET!,
      (err: any, decoded: any) => {
        if (err) throw err;
        else {
          const accessToken = jwt.sign(
            { user: decoded.user },
            process.env.ACCESS_TOKEN_SECRET!,
            {
              expiresIn: process.env.TOKEN_EXPIRATION,
            }
          );
          return res.send(
            new ApiResponse(true, "", { accessToken: accessToken })
          );
        }
      }
    );
  } catch (error) {
    if (error instanceof CustomError) {
      res.send(new ApiResponse(false, error.message, null));
    } else {
      res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.get(
  "/google",
  passport.authenticate("google", { scope: ["profile", "email"] })
);

router.get(
  "/google/callback",
  passport.authenticate("google", { session: false }),
  async (req, res) => {
    //Create new tokens and save to database
    const accessToken = jwt.sign(
      { user: (req as any).user },
      process.env.ACCESS_TOKEN_SECRET!,
      {
        expiresIn: process.env.TOKEN_EXPIRATION,
      }
    );
    const refreshToken = jwt.sign(
      { user: (req as any).user },
      process.env.REFRESH_TOKEN_SECRET!
    );
    await saveRefreshToken((req as any).user.id, refreshToken);

    res.json({
      accessToken: accessToken,
      refreshToken: refreshToken,
    });
  }
);

export default router;
