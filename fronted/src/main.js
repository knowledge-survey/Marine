import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const resizeObserverError = window.ResizeObserver
if (resizeObserverError) {
  const origError = window.onerror
  // eslint-disable-next-line no-unused-vars
  window.onerror = function() {
    const message = arguments[0]
    if (message && message.toString().indexOf('ResizeObserver loop') !== -1) {
      return true
    }
    if (origError) {
      return origError.apply(this, arguments)
    }
  }
}

app.mount('#app')
