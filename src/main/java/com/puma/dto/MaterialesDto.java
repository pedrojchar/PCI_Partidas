package com.puma.dto;

public class MaterialesDto {
	
	private String id;
	private String unidad;
	private String nombre;
	private String punit;
	private String co;
	private String desperdicio;
	
	
	public MaterialesDto() {
	}
	
	public MaterialesDto(String id, String unidad) {
		super();
		this.id = id;
		this.unidad = unidad;
	}

	
	
	public MaterialesDto(String id, String unidad, String nombre, String punit, String co, String desperdicio) {
		super();
		this.id = id;
		this.unidad = unidad;
		this.nombre = nombre;
		this.punit = punit;
		this.co = co;
		this.desperdicio = desperdicio;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPunit() {
		return punit;
	}

	public void setPunit(String punit) {
		this.punit = punit;
	}

	public String getCo() {
		return co;
	}

	public void setCo(String co) {
		this.co = co;
	}

	public String getDesperdicio() {
		return desperdicio;
	}

	public void setDesperdicio(String desperdicio) {
		this.desperdicio = desperdicio;
	}
	
	
	
}