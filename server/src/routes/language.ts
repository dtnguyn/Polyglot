import { Router } from "express";
import { Language } from "../entity/Language";
import {
  chooseLanguages,
  getLanguageReports,
  getLearningLanguages,
} from "../controllers/LanguageController";

import ApiResponse from "../utils/ApiResponse";
import { checkAuthentication } from "../utils/middlewares";
import CustomError from "../utils/CustomError";
import { LanguageReport } from "src/utils/types";

const router = Router();

//GET ROUTES
router.get("/", checkAuthentication, async (req, res) => {
  try {
    const userId = (req as any).user.id;

    const languages = await getLearningLanguages(userId);
    res.send(new ApiResponse<Language[]>(true, "", languages));
  } catch (error) {
    if (error instanceof CustomError) {
      res.send(new ApiResponse(false, error.message, null));
    } else {
      res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

router.get("/report", checkAuthentication, async (req, res) => {
  try {
    const userId = (req as any).user.id;

    const result = await getLanguageReports(userId);
    res.send(new ApiResponse<Array<LanguageReport>>(true, "", result));
  } catch (error) {
    console.log(error);
  }
});

//POST ROUTES
router.post("/save", checkAuthentication, async (req, res) => {
  try {
    const languages = req.body.languages;
    const userId = (req as any).user.id;

    await chooseLanguages(languages, userId);
    res.send(new ApiResponse(true, "", null));
  } catch (error) {
    if (error instanceof CustomError) {
      res.send(new ApiResponse(false, error.message, null));
    } else {
      res.send(new ApiResponse(false, "Something went wrong", null));
    }
  }
});

export default router;
