<template>
    <div class="JsonArea">
        <pre v-html="prettyJsonData"></pre>
    </div>
</template>

<script>
    export default {
        name: "JsonArea",
        props: {
            jsonText: [Object, Array]
        },
        computed: {
            prettyJsonData: function () {
                let json = this.jsonText;
                // replacing escaped symbols from Jira error
                if (json.errorText && json.errorText.message && typeof(json.errorText.message) !== 'object') {
                    let message = json.errorText.message;
                    json.errorText.message = JSON.parse(message.replace(/\\/g, ''));
                }
                json = JSON.stringify(json, null, 2);
                json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
                json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+]?\d+)?)/g,
                    function (match) {
                    let cls = 'number';
                    if (/^"/.test(match)) {
                        if (/:$/.test(match)) {
                            cls = 'key'
                        } else {
                            cls = 'string'
                        }
                    } else if (/true|false/.test(match)) {
                        cls = 'boolean'
                    } else if (/null/.test(match)) {
                        cls = 'null'
                    }
                    return '<span class="' + cls + '" >' + match + '</span>'
                });

                return json.replace(/(?:\\r\\n|\\r|\\n)/g, '575757\\\n').replace(/575757\\/g, '').replace(/\\"/g, '"');
            }
        }
    }
</script>

<style scoped>
    pre {
        outline: 2px solid #ccc;
        padding: 5px;
        margin: 5px;
        text-align: left;
        font-family: monospace;
        white-space: pre-wrap;
        word-break: break-word;
        overflow: auto;
        background: #fbfbfb;
        max-height: 50vh;
    }

    .JsonArea >>> .string {
        color: green;
    }

    .JsonArea >>> .number {
        color: darkorange;
    }

    .JsonArea >>> .boolean {
        color: blue;
    }

    .JsonArea >>> .null {
        color: magenta;
    }

    .JsonArea >>> .key {
        color: #7e2c24;
    }
</style>