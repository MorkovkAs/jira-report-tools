<template>
    <div class="Issue">
        <JsonArea v-bind:jsonText="jsonInfo"/>
    </div>
</template>

<script>
    import JsonArea from './JsonArea.vue'

    export default {
        name: "Issue",
        components: {
            JsonArea
        },
        props: {
            jsonInfo: [Object, Array]
        }
    }
</script>

<style scoped>

</style>