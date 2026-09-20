# Verificación de Serializable

- BaseEntity: ya implementaba Serializable, pero SIN serialVersionUID declarado.
- MedicalRecord: se agregó 'implements Serializable' explícito + serialVersionUID = 1L ✅
- Prescription: se agregó 'implements Serializable' explícito + serialVersionUID = 1L ✅

Verificado por: Erika Fuentes, 19/09/2026