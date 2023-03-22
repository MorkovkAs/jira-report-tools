<template>
    <div id="app">
        <img alt="Vue logo" src="./assets/logo.png">
        <RequestList/>
        <Footer/>
    </div>
</template>

<script>
    import RequestList from "./components/RequestList"
    import Footer from './components/Footer.vue'

    export default {
        name: 'App',
        components: {
            RequestList,
            Footer
        }
    }
</script>

<style>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        margin-top: 20px;
    }
</style>
