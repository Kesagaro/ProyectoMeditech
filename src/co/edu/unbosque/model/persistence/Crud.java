package co.edu.unbosque.model.persistence;

public interface Crud<T> {
	/**
	 * La interfaz CRUD define las operaciones básicas de crear, mostrar, actualizar
	 * y eliminar que deben implementar las clases que manejen objetos de tipo
	 * genérico.
	 *
	 * @param <T> El tipo de objeto que se manipulará en las operaciones.
	 */
	public void crear(T data);

	public String mostrar();

	public String actualizar(int index, T newData);

	public String eliminarPorNombre(String name);

	public String checkearIndex(int index);
}
