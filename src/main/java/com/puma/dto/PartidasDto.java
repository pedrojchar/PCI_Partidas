package com.puma.dto;

public class PartidasDto {

	private String id;
	private String tabla;
	private String idItem;
	private String cantidad;
	private String nombre;
	private String descripcion;
	private String nroPartida;
	private String unidad;
	private String cantidadPartida;
	private String rendimiento;
	private String unidadRendimiento;
	private String precio;
	private String porcentajePerdidas;
	private String unidadPartida;

	
	public PartidasDto() {
	}

	public PartidasDto(String id, String tabla, String idItem, String cantidad, String nombre, String descripcion,
			String nroPartida, String unidad, String cantidadPartida, String rendimiento, String unidadRendimiento,
			String precio, String porcentajePerdidas, String unidadPartida) {
		super();
		this.id = id;
		this.tabla = tabla;
		this.idItem = idItem;
		this.cantidad = cantidad;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nroPartida = nroPartida;
		this.unidad = unidad;
		this.cantidadPartida = cantidadPartida;
		this.rendimiento = rendimiento;
		this.unidadRendimiento = unidadRendimiento;
		this.precio = precio;
		this.porcentajePerdidas = porcentajePerdidas;
		this.unidadPartida = unidadPartida;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public String getIdItem() {
		return idItem;
	}

	public void setIdItem(String idItem) {
		this.idItem = idItem;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNroPartida() {
		return nroPartida;
	}

	public void setNroPartida(String nroPartida) {
		this.nroPartida = nroPartida;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public String getCantidadPartida() {
		return cantidadPartida;
	}

	public void setCantidadPartida(String cantidadPartida) {
		this.cantidadPartida = cantidadPartida;
	}

	public String getRendimiento() {
		return rendimiento;
	}

	public void setRendimiento(String rendimiento) {
		this.rendimiento = rendimiento;
	}

	public String getUnidadRendimiento() {
		return unidadRendimiento;
	}

	public void setUnidadRendimiento(String unidadRendimiento) {
		this.unidadRendimiento = unidadRendimiento;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public String getPorcentajePerdidas() {
		return porcentajePerdidas;
	}

	public void setPorcentajePerdidas(String porcentajePerdidas) {
		this.porcentajePerdidas = porcentajePerdidas;
	}

	public String getUnidadPartida() {
		return unidadPartida;
	}

	public void setUnidadPartida(String unidadPartida) {
		this.unidadPartida = unidadPartida;
	}
	
	
}