import globals from "globals";
import pluginJs from "@eslint/js";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";
import eslintPluginUnicorn from "eslint-plugin-unicorn";
import { FlatCompat } from "@eslint/eslintrc";

const compat = new FlatCompat({
  // import.meta.dirname is available after Node.js v20.11.0
  baseDirectory: import.meta.dirname,
});

/** @type {import('eslint').Linter.Config[]} */
const config = [
  { ignores: [".next/**", "public/**", "next.config.js", "postcss.config.js"] },
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"] },
  { languageOptions: { globals: { ...globals.browser, ...globals.node } } },
  pluginJs.configs.recommended,
  ...tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  eslintPluginUnicorn.configs["recommended"],
  ...compat.config({
    extends: ["next"],
    settings: {
      next: {
        rootDir: ".",
      },
    },
  }),
  ...compat.config({
    extends: ["plugin:drizzle/all"],
  }),
  {
    rules: {
      "react/react-in-jsx-scope": "off",
      "@typescript-eslint/no-unused-vars": [
        "error", // or "error"
        {
          argsIgnorePattern: "^_",
          varsIgnorePattern: "^_",
          caughtErrorsIgnorePattern: "^_",
        },
      ],
      "unicorn/prevent-abbreviations": "off",
      "unicorn/filename-case": "off",
      "unicorn/no-nested-ternary": "off",
      "unicorn/no-document-cookie": "off",
      "unicorn/no-null": "off",
    },
  },
  {
    files: ["**/*.{jsx,tsx}"],
    rules: {
      "no-console": [
        "error",
        {
          allow: ["warn", "error"],
        },
      ],
    },
  },
];
export default config;
