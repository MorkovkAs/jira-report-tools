<template>
    <div class="RequestList">
        <h3>Choose the type of request</h3>
        <p>
            <select v-model="requestTypeSelected">
                <option v-for="option in requestOptions" v-bind:key="option.key" v-bind:value="option.key">
                    {{ option.text }}
                </option>
            </select>
        </p>
        <p><input v-model="requestKey" v-bind:disabled=!requestTypeSelected :placeholder="[[ searchParamType ]]"></p>
        <button v-on:click="sendRequest" v-bind:disabled=!requestKey>Send</button>
        <h3>Result</h3>
        <section v-if="errored">
            <p>{{errorText}}</p>
        </section>
        <section v-else>
            <div v-if="loading">Loading...</div>
            <span> {{ info }} </span>
        </section>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: "JiraRequests",

        data() {
            return {
                requestTypeSelected: '',
                requestKey: '',
                requestOptions: [
                    {text: 'Get issue', key: 'getIssue', searchParam: 'Enter issue key'},
                    {text: 'Get release issues', key: 'getIssueInRelease', searchParam: 'Enter release'},
                    {text: 'Get release notes', key: 'getReleaseNotes', searchParam: 'Enter release'}
                ],
                info: null,
                loading: false,
                errored: false,
                errorText: 'Sorry, we\'re not able to retrieve this information at the moment, please try back later'
            }
        },
        computed: {
            searchParamType: function () {
                let item = this.requestOptions.find(item => item.key === this.requestTypeSelected);
                if (item) {
                    return item.searchParam
                }
                return null
            }
        },
        methods: {
            sendRequest: function () {
                //TODO different requestUrls
                let requestUrl = "http://localhost:8181/task/byKey?jiraKey=";
                this.loading = true;

                axios
                    .get(requestUrl + this.requestKey)
                    .then(response => {
                        if (response.status === 200) {
                            console.log("200");
                            let output = Buffer.from(response.data).toString();
                            let json = JSON.parse(output); // this fails because it is cut short
                            console.log(json);
                        }
                        this.info = response.data;
                        this.errored = false;
                    })
                    .catch(error => {
                        if (error.status === 404) {
                            //TODO errors catching
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

</style>