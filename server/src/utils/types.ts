export type WordDetailJSON = {
  value: string;
  language: string;
  topics: string;
  pronunciations: PronunciationJSON[];
  definitions: DefinitionJSON[];
};

export type WordDetailSimplifyJSON = {
  value: string;
  language: string;
  topics: string;
  mainDefinition: string;
  pronunciationAudio: string | null;
  pronunciationSymbol: string | null;
};

export type PronunciationJSON = {
  audio: string;
  symbol: string;
};

export type DefinitionJSON = {
  meaning: string;
  partOfSpeech: string;
  example: string;
};

export type FeedJSON = {
  id: string;
  type: string;
  title: string;
  thumbnail: string | null;
  author: string | null;
  topic: string | null;
  language: string;
  url: string;
  description: string;
  publishedDate: string | null;
};

export type FeedDetailJSON = {
  id: string;
  type: string;
  title: string;
  thumbnail: string;
  content: NewsDetailJSON | VideoDetailJSON;
};

export type NewsDetailJSON = {
  value: string;
  publishedDate: string;
  source: string;
  author: string;
  images: string[];
};

export type VideoDetailJSON = {
  value: any;
};

export type SubtitlePart = {
  start: number;
  dur: number;
  end: number;
  text: string;
  translatedText: string;
  lang: string;
  translatedLang: string;
};

export type LanguageReport = {
  languageId: string;
  savedWordCount: number;
  wordTopicReports: Array<WordTopicReport>;
};

export type WordTopicReport = {
  languageId: string;
  value: string;
  wordCount: number;
};

export type VerifyCodeAction = {
  actionTitle: "reset_password"
  actionValue: any | null
}