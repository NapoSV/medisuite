| Pantalla   | 390px | 768px | 1280px | Fix aplicado                               |
|------------|-------|-------|--------|--------------------------------------------|
| Login      | ✅    | ✅    | ✅     | —                                          |
| Dashboard  | ✅    | ✅    | ✅     | — (grid ya era responsive 1/2/4)           |
| Pacientes  | ✅ | ✅ | ✅   | tabla con overflow-x-auto y min-w-[480px]; buscador apilado en móvil (flex-col sm:flex-row); botones con flex-wrap; padding p-4 |
| Doctores   | ⏳ | ⏳ | ⏳ | No probada: /doctores es solo un placeholder ("pendiente D-07 Díaz"), D-07 aún no está en develop. Pendiente de revisar cuando se integre |
| Citas      | N/A   | N/A   | N/A    | Sin ruta en App.tsx aún                    |
| Expediente | N/A   | N/A   | N/A    | Sin ruta en App.tsx aún                    |
| Perfil     | ✅    | ✅    | ✅     | — (sin datos por error 403)                |