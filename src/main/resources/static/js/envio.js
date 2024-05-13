document.addEventListener('DOMContentLoaded', function () {
    const fechaEntregaInput = document.getElementById('fechaEntregaLI');
    const fechaEntregaVueltaAlmacenInput = document.getElementById('fechaEntregaVueltaAlmacenLI');

    function toggleInputDisable() {
        if (fechaEntregaInput.value) {
            fechaEntregaVueltaAlmacenInput.disabled = true;
        } else {
            fechaEntregaVueltaAlmacenInput.disabled = false;
        }

        if (fechaEntregaVueltaAlmacenInput.value) {
            fechaEntregaInput.disabled = true;
        } else {
            fechaEntregaInput.disabled = false;
        }
    }

    fechaEntregaInput.addEventListener('change', toggleInputDisable);
    fechaEntregaVueltaAlmacenInput.addEventListener('change', toggleInputDisable);

    // Run on load
    toggleInputDisable();
});