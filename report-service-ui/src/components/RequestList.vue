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
                    <p>
                        <label> Project Code:
                            <input v-model="projectCode" v-bind:disabled=!requestTypeSelected style=" width: 60px; margin: 0px">
                        </label>
                    </p>
                    <p>
                        <label> Limit by:
                            <input v-model="requestLimit" v-bind:disabled=!requestTypeSelected style=" width: 30px; margin: 0px">
                        </label>
                    </p>
                    <p>
                        <label> Secret code:
                            <input v-model="token" v-bind:disabled=!requestTypeSelected style=" width: 30px; margin: 0px">
                        </label>
                    </p>
                    <!--<Recaptcha
                            :sitekey=recaptchaKey
                            :loadRecaptchaScript="true"
                            ref="recaptcha"
                            @verify="onVerify"
                            @expired="onExpired"
                            align="center"/>-->
                    <!--<button v-on:click="resetRecaptcha"> Reset ReCAPTCHA</button>-->
                    <p>
                        <button v-on:click="sendRequest" v-bind:disabled=!isOkToSend>Send</button>
                    </p>
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
    //import Recaptcha from 'vue-recaptcha';
    import axios from "axios"

    export default {
        name: "JiraRequests",
        components: {
            Issue,
            Error//,
            //Recaptcha
        },

        data() {
            return {
                recaptchaKey: '6LfuA_8UAAAAAIaccql90DkNTuR9BL6W6bsyKDtO',
                requestTypeSelected: '',
                requestKey: '',
                requestLimit: 15,
                projectCode: 'DM',
                token: '',
                requestOptions: [
                    {
                        text: 'Get issue',
                        key: 'getIssue',
                        searchParam: 'Enter issue key',
                        url: '/api/task/byKey?jiraKey='
                    },
                    {
                        text: 'Get issues by jql',
                        key: 'getIssueByJql',
                        searchParam: 'Enter jql string',
                        url: '/api/task/byJql?jql='
                    },
                    {
                        text: 'Get release issues',
                        key: 'getIssueInRelease',
                        searchParam: 'Enter release',
                        url: '/api/task/byRelease?jiraRelease='
                    },
                    {
                        text: 'Get release info',
                        key: 'getReleaseInfo',
                        searchParam: 'Enter release',
                        url: '/api/release/infoByRelease?jiraRelease='
                    },
                    {
                        text: 'Get release notes',
                        key: 'getReleaseNotes',
                        searchParam: 'Enter release',
                        url: '/api/release/getReleaseNote?jiraRelease='
                    },
                    {
                        text: 'Get release notes as string',
                        key: 'getReleaseNotesString',
                        searchParam: 'Enter release',
                        url: '/api/release/getReleaseNoteString?jiraRelease='
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
                firstRequestSend: false,
                verified: false
            }
        },
        computed: {
            isOkToSend() {
                return /**this.verified &&**/ this.requestKey && this.token;
            },
            searchParamType: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                if (item) {
                    return item.searchParam
                }
                return 'Enter ...'
            }
        },
        methods: {
            onVerify: function () {
                this.verified = true
            },
            onExpired: function () {
                this.verified = false
            },
            resetRecaptcha() {
                this.verified = false
                this.$refs.recaptcha.reset()
            },

            sendRequest: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                let resultUrl = item.url + this.requestKey;
                if (item.key !== 'getIssue') {
                    resultUrl += '&limit=' + this.requestLimit + '&jiraProject=' + this.projectCode
                }

                this.firstRequestSend = true;
                this.loading = true;
                this.response = null;

                axios
                    .get(resultUrl, { 'headers': { 'Authorization': this.token } })
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