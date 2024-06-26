class Usuario extends Persona {
    private int id;

    public Usuario(int id, String nombre, String email) {
        super(nombre, email);
        this.id = id;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("ID: " + id + ", Nombre: " + getNombre() + ", Email: " + getEmail());
    }
}
