module.exports = {
    root: true,
    env: {
        node: true,
    },
    extends: [
        'plugin:vue/recommended',
    ],
    parserOptions: {
        ecmaVersion: 2020,
    },
    rules: {
        "no-mutating-props": "off",
        "vue/no-mutating-props": "off",
    },
    // These are added if you chose also to install Jest plugin for Vue CLI
    // With my own modifications here as an example
    overrides: [
        {
            files: [
                './src/**/__tests__/*.spec.{j,t}s',
                './src/**/__mock__/*.{j,t}s',
            ],
            env: {
                jest: true,
            },
            rules: {
                'no-unused-expressions': 0,
            },
        },
    ],
};