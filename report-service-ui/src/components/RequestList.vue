<template>
    <div class="RequestList">
        <table>
            <tbody>
            <tr>
                <td style="width: 40%">
                    <h3>Choose the type of request</h3>
                    <p>
                        <select v-model="requestTypeSelected" autofocus>
                            <option disabled value="">Choose option</option>
                            <option v-for="option in requestOptions" v-bind:key="option.key" v-bind:value="option.key">
                                {{ option.text }}
                            </option>
                        </select>
                        <input v-model="requestKey" v-bind:disabled=!requestTypeSelected :placeholder="[[ searchParamType ]]">
                    </p>
                    <label> Limit by:
                        <input v-model="requestLimit" v-bind:disabled=!requestTypeSelected style=" width: 30px; margin: 0px">
                    </label>
                    <p><button v-on:click="sendRequest" v-bind:disabled=!requestKey>Send</button></p>
                </td>
                <td style="width: 60%">
                    <h3 v-if="firstRequestSend">Result</h3>
                    <section v-if="errored">
                        <Error v-bind:error="error" v-bind:error-status="errorStatus"/>
                    </section>
                    <section v-else>
                        <div v-if="loading">Loading...</div>
                        <Issue v-if="response" v-bind:jsonInfo="response"/>
                    </section>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
    import Issue from './Issue.vue'
    import Error from './Error.vue'
    import axios from "axios";

    export default {
        name: "JiraRequests",
        components: {
            Issue,
            Error
        },

        data() {
            return {
                requestTypeSelected: '',
                requestKey: '',
                requestLimit: 15,
                requestOptions: [
                    {
                        text: 'Get issue',
                        key: 'getIssue',
                        searchParam: 'Enter issue key',
                        url: '/task/byKey?jiraKey='
                    },
                    {
                        text: 'Get issues by jql',
                        key: 'getIssueByJql',
                        searchParam: 'Enter jql string',
                        url: '/task/byJql?jql='
                    },
                    {
                        text: 'Get release issues',
                        key: 'getIssueInRelease',
                        searchParam: 'Enter release',
                        url: '/task/byRelease?jiraRelease='
                    },
                    {
                        text: 'Get release notes',
                        key: 'getReleaseNotes',
                        searchParam: 'Enter release',
                        url: '/release/infoByRelease?jiraRelease='
                    }
                ],
                response: null,
                error: {
                    errorCode: Int8Array,
                    errorText: String
                },
                errorStatus: null,
                loading: false,
                errored: false,
                firstRequestSend: false
            }
        },
        computed: {
            searchParamType: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                if (item) {
                    return item.searchParam
                }
                return 'Enter ...'
            }
        },
        methods: {
            sendRequest: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                let resultUrl = 'http://localhost:8181' + item.url + this.requestKey;
                if (item.key !== 'getIssue') {
                    resultUrl += '&limit=' + this.requestLimit
                }

                this.firstRequestSend = true;
                this.loading = true;
                this.response = null;

                axios
                    .get(resultUrl)
                    .then(response => {
                        this.response = response.data;
                        this.errored = false;
                    })
                    .catch(error => {
                        if (error.response) {
                            this.error.errorCode = error.response.status;
                            this.error.errorText = error.response.data;
                            this.errorStatus = error.response.data.status;

                            if (error.response.status === 401 || error.response.status === 403) {
                                this.errorStatus = 'Authentication failed'
                            }
                        } else {
                            this.error.errorCode = 523;
                            this.error.errorText = error.message + ': Origin Is Unreachable';
                            this.errorStatus = 'ORIGIN_IS_UNREACHABLE';
                        }
                        console.log(error);
                        this.errored = true;
                    })
                    .finally(() => (this.loading = false));
            }
        }
    }
</script>

<style scoped>
    input {
        width: 100px;
        height: 20px;
        vertical-align: top;
        margin: 5px;
    }

    select {
        border: 1px solid #ccc;
        vertical-align: top;
        height: 20px;
        margin: 5px;
        cursor: pointer;
    }

    input, select {
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }

    table {
        width: 100%;
    }

    td {
        vertical-align: top;
    }

    button {
        font-size: 16px;
        width: 130px;
        background-color: #42b983;
        border: none;
        color: white;
        padding: 5px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        border-radius: 4px;
        cursor: pointer;
    }

    input:disabled, button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
</style>