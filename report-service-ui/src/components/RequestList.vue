<template>
    <div class="RequestList">
        <table>
            <tbody>
            <tr>
                <td style="width: 40%">
                    <h3>Choose the type of request</h3>
                    <p>
                        <select v-model="requestTypeSelected">
                            <option disabled value="">Choose option</option>
                            <option v-for="option in requestOptions" v-bind:key="option.key" v-bind:value="option.key">
                                {{ option.text }}
                            </option>
                        </select>
                    </p>
                    <p>
                        <select v-model="releasesSelected" autofocus v-on:change="onChangeRelease">
                            <option disabled value="">Choose option</option>
                            <option v-for="option in releasesOptions" v-bind:key="option.key" v-bind:value="option.key">
                                {{ option.text }}
                            </option>
                        </select>
                        <input v-model="requestKey" v-bind:disabled=!requestTypeSelected :placeholder="[[ searchParamType ]]" style="width: 50px">
                    </p>
                    <p>
                      <label for="example-datepicker">Last release date: </label>
                      <date-picker v-model="lastReleaseDate"
                                     v-bind:disabled=!requestTypeSelected
                                     format="YYYY-MM-DD"
                                     value-type="format"
                                     type="date"
                                     style="height: 10px; width: 150px">
                        </date-picker>
                    </p>
                    <p>
                        <label> Project code:
                            <input v-model="projectCode" disabled style=" width: 60px; margin: 0px">
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
                    <p>
                        <button v-on:click="sendRequest" v-bind:disabled=!isOkToSend>Send</button>
                        <button v-on:click="copyResponse" v-bind:disabled=!isOkToCopyResult
                                style="background-color: #34495e">Copy</button>
                    </p>
                  <!--<Recaptcha
                        :sitekey=recaptchaKey
                        :loadRecaptchaScript="true"
                        ref="recaptcha"
                        @verify="onVerify"
                        @expired="onExpired"
                        align="center"/>
                    <button v-on:click="resetRecaptcha"> Reset ReCAPTCHA</button>
                    <p>
                        <button v-on:click="createOrUpdateConfPage" v-bind:disabled=!isOkToCreateConfPage
                                style="background-color: #34495e; width: 270px">Send to Confluence</button>
                    </p>-->
                    <div style="display: inline-block">
                              <details class="block-round-content">
                                  <summary style="cursor: pointer">
                                    <font-awesome-icon icon="fa-solid fa-circle-info" size="lg"
                                                       fade style="--fa-animation-duration: 4s; --fa-fade-opacity: 0.7"
                                                       title="Теги в Jira для автоформирования release notes"
                                    />
                                    Useful tags for Jira comments:
                                  </summary>
                                  <p/>
                                  <div style="display: inline-block; text-align: left;">
                                      <p>h2. Исходные коды:</p>
                                      <p>h2. Артефакты:</p>
                                      <p>h2. Новые функции и исправления:</p>
                                      <p>h2. Порядок установки:</p>
                                      <p>h2. План тестирования у заказчика:</p>
                                      <p>h2. План отката:</p>
                                  </div>
                              </details>
                    </div>
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
    import axios from "axios";
    import DatePicker from 'vue2-datepicker';
    import 'vue2-datepicker/index.css';

    export default {
        name: "JiraRequests",
        components: {
            Issue,
            Error,
            DatePicker//,
            //Recaptcha
        },

        data() {
            return {
                recaptchaKey: '6LfuA_8UAAAAAIaccql90DkNTuR9BL6W6bsyKDtO',
                requestKey: '1.3.0',
                requestLimit: 15,
                projectCode: 'DMKZD',
                token: '',
                lastReleaseDate: this.createDate(-14,0,0),
                requestTypeSelected: 'getReleaseNotesString',
                requestOptions: [
                    {
                        text: 'Get release issues',
                        key: 'getIssueInRelease',
                        type: 'release',
                        searchParam: 'Enter release',
                        url: '/api/task/byRelease?jiraRelease='
                    },
                    {
                        text: 'Get release info',
                        key: 'getReleaseInfo',
                        type: 'release',
                        searchParam: 'Enter release',
                        url: '/api/release/infoByRelease?jiraRelease='
                    },
                    {
                        text: 'Get release notes',
                        key: 'getReleaseNotes',
                        type: 'release',
                        searchParam: 'Enter release',
                        url: '/api/release/getReleaseNote?jiraRelease='
                    },
                    {
                        text: 'Get release notes as string',
                        key: 'getReleaseNotesString',
                        type: 'release',
                        searchParam: 'Enter release',
                        url: '/api/release/getReleaseNoteString?jiraRelease='
                    }
                ],
                releasesSelected: 'kzd2',
                releasesOptions: [{
                  text: 'Offline Merge',
                  key: 'offline-merge',
                  value: 'offline-merge-',
                  project: 'DMKZD'
                }, {
                  text: 'KZD 2',
                  key: 'kzd2',
                  value: 'kzd2-',
                  project: 'DMKZD'
                }, {
                  text: 'KSRD',
                  key: 'ksrd',
                  value: '',
                  project: 'DM'
                }],
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
            isOkToCopyResult() {
                return /**this.verified &&**/ this.isOkToSend && this.response !== null;
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
            onChangeRelease() {
                let item = this.releasesOptions.find(item => item.key === this.releasesSelected);
                this.projectCode = item.project
            },

            sendRequest: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                let resultUrl = item.url;
                if (item.type === 'release') {
                    let releaseCode = this.releasesOptions.find(item => item.key === this.releasesSelected);
                    resultUrl += releaseCode.value + this.requestKey
                    resultUrl += '&jiraProject=' + this.projectCode + '&limit=' + this.requestLimit + '&releaseNumber=' + this.requestKey
                    resultUrl += '&lastReleaseDate='+ this.lastReleaseDate
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
            },

            copyResponse: function () {
                if (navigator.clipboard && window.isSecureContext) {
                    // navigator clipboard api method'
                    return navigator.clipboard.writeText(this.response);
                } else {
                    // text area method
                    let textArea = document.createElement("textarea");
                    textArea.value = this.response;
                    // make the textarea out of viewport
                    textArea.style.position = "fixed";
                    textArea.style.left = "-999999px";
                    textArea.style.top = "-999999px";
                    document.body.appendChild(textArea);
                    textArea.focus();
                    textArea.select();
                    return new Promise((res, rej) => {
                        // here the magic happens
                        document.execCommand('copy') ? res() : rej();
                        textArea.remove();
                    });
                }
            },

            createDate: function (days, months, years) {
                let date = new Date();
                date.setDate(date.getDate() + days);
                date.setMonth(date.getMonth() + months);
                date.setFullYear(date.getFullYear() + years);
                return date.toISOString().split('T')[0];
            }
        }
    }
</script>

<style scoped>
    input {
        width: 100px;
        height: 20px;
        vertical-align: center;
        margin-left: 5px;
        margin-right: 5px;
    }

    select {
        border: 1px solid #ccc;
        vertical-align: center;
        height: 20px;
        margin-left: 5px;
        margin-right: 5px;
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
        vertical-align: center;
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
        border-radius: 5px;
        cursor: pointer;
        margin-left: 5px;
        margin-right: 5px;
    }

    input:disabled, button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .block-round-content  {
      background: #cdf6e4; /* Цвет фона */
      padding: 10px; /*  Поля вокруг текста */
      font-size: 12px;
      border-radius: 10px;
      line-height: 0.5;
    }

    summary::marker {
      content: "";
    }
</style>