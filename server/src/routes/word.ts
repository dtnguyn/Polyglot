import { Request, Response, Router } from "express";
import {
  getDailyRandomWords,
  getDefinition,
  getSavedWords,
  getWordAutoCompletes,
  toggleSaveWord,
} from "../controllers/WordController";
import { User } from "../entity/User";
import { checkAuthentication } from "./auth";

const router = Router();

router.get("/daily", async (req, res) => {
  try {
    let dailyWordCount = 3;
    if (req.user) {
      dailyWordCount = (req.user as User).dailyWordCount;
    }

    const language = req.query.language as string;

    const words = await getDailyRandomWords(dailyWordCount, language);

    res.json(words);
  } catch (error) {
    res.status(400).send({
      message: error.message,
    });
  }
});

router.get("/definition", async (req, res) => {
  try {
    const language = req.query.language as string;
    const word = req.query.word as string;

    if (!word || !language)
      throw new Error("Please provide the word and definition language");

    const definition = await getDefinition(word, language);
    if (!definition)
      throw new Error("Couldn't find definition for the word provided!");
    res.json(definition);
  } catch (error) {
    res.status(400).send({
      message: error.message,
    });
  }
});

router.get("/autocomplete", async (req, res) => {
  try {
    const text = req.query.text as string;
    const language = req.query.language as string;

    if (!text || !language)
      throw new Error("Please provide text and language!");
    const results = await getWordAutoCompletes(language, text);
    // console.log(results);
    res.send(results);
  } catch (error) {
    res.status(400).send({
      message: error.message,
    });
  }
});

router.get("/save", checkAuthentication, async (req, res) => {
  try {
    const userId = (req as any).user.id;
    const savedWords = await getSavedWords(userId);
    res.json(savedWords);
  } catch (error) {
    res.status(400).send({
      message: error.message,
    });
  }
});

//Post routes
router.post("/save", checkAuthentication, async (req, res) => {
  try {
    const word = req.body.word;
    const language = req.body.language;
    const userId = (req as any).user.id;
    await toggleSaveWord(word, language, userId);
    res.json({ status: true });
  } catch (error) {
    console.log(error);
    res.status(400).send({
      message: error.message,
    });
  }
});

export default router;
