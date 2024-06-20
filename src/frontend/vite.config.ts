import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      'vue': 'vue/dist/vue.esm-bundler.js', // from issue: Configure your bundler to alias "vue" to "vue/dist/vue.esm-bundler.js". 
    },
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    }
  }
})
