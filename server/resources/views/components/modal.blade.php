<div class="modal fade" id="userModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title">Datos del Usuario</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body" id="modal-body">
            </div>

            <div class="modal-footer">
                <form action="{{ route('admin.logout') }}" method="POST">
                    @csrf
                    @method('POST')
                    <button type="submit" class="btn" data-bs-dismiss="modal">Cerrar Sesion</button>
                </form>
            </div>

        </div>
    </div>
</div>