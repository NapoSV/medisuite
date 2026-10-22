export type TabId = 'resumen' | 'historial' | 'signos' | 'recetas' | 'documentos'

export const tabId = (id: TabId) => `expediente-tab-${id}`
export const panelId = (id: TabId) => `expediente-panel-${id}`