<template>
    <div class="Error">
        <p>Sorry, we're not able to retrieve this information at the moment</p>
        <p> Error code: {{error.errorCode}} {{errorStatus}} </p>
        <button v-on:click="showFullError = !showFullError">
            Show error
        </button>
        <transition name="fade">
            <section v-if="showFullError">
                <JsonArea v-bind:jsonText="error"/>
            </section>
        </transition>
    </div>
</template>

<script>
    import JsonArea from './JsonArea.vue'

    export default {
        name: "Error",
        components: {
            JsonArea
        },
        props: {
            error: Object,
            errorStatus: String
        },
        data() {
            return {
                showFullError: false
            }
        }
    }
</script>

<style scoped>
    button {
        font-size: 16px;
        width: 130px;
        background-color: #896d5f;
        border: none;
        color: white;
        padding: 5px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        border-radius: 4px;
        cursor: pointer;
    }
</style>