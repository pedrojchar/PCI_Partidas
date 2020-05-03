package com.puma.gestiondocumental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puma.dto.EquiposDto;
import com.puma.dto.ManoDto;
import com.puma.dto.MaterialesDto;
import com.puma.dto.PartidasDto;
import com.puma.model.ResponseModel;
import com.puma.service.util.StringNumberComparator;
import com.puma.service.util.Utils;

public class Gestion {

	private Utils utils;
	DataFormatter dataFormatter;
	private HashMap<String, MaterialesDto> hashMateriales;
	private HashMap<String, EquiposDto> hashEquipos;
	private HashMap<String, ManoDto> hashManos;

	private List<MaterialesDto> lstMateriales = new ArrayList<MaterialesDto>();
	private List<EquiposDto> lstEquipos = new ArrayList<EquiposDto>();
	private List<ManoDto> lstManos = new ArrayList<ManoDto>();

	private List<PartidasDto> lstPartidas;

	private static final String SQL_INSERT_MATERIALES = "INSERT INTO MATERIALES (ID, NOMBRE, UNIDAD) VALUES (?,?,?)";
	private static final String SQL_INSERT_EQUIPOS = "INSERT INTO EQUIPOS (ID, NOMBRE, UNIDAD) VALUES (?,?,?)";
	private static final String SQL_INSERT_MANOS = "INSERT INTO MANO_OBRA (ID, NOMBRE, UNIDAD) VALUES (?,?,?)";
	private static final String SQL_INSERT_PARTIDA = "INSERT INTO PARTIDAS (ID, TABLA, ID_ITEM, CANTIDAD, DESCRIPCION, NRO_PARTIDA, UNIDAD, CANTIDAD_PARTIDA, RENDIMIENTO, UNIDAD_RENDIMIENTO, PORCENTAJE, UNIDAD_PARTIDA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_SELECT_ITEM_BY_NAME = "SELECT * FROM {tabla}";
	private static final String ARCHIVO_RESULTANTE = "C:\\Users\\pedro\\OneDrive\\Documents\\partidas\\resultado.xlsx";
	
	public Gestion() {
		this.utils = new Utils();
		hashMateriales = new HashMap<>();
		hashEquipos = new HashMap<>();
		hashManos = new HashMap<>();
		dataFormatter = new DataFormatter();
		lstPartidas = new ArrayList<PartidasDto>();
	}

	public void procesarData(ResponseModel response)
			throws IOException, SQLException, ServletException, ClassNotFoundException {
		Workbook workbook = loadExcel();

		workbook.forEach(hoja -> {
			procesarHojaPartida(hoja);
		});
		lstMateriales.remove("");
		lstMateriales.remove("Descripción");
		lstEquipos.remove("");
		lstEquipos.remove("Descripción");
		lstManos.remove("");
		lstManos.remove("Descripción");
		crearDataOnDbBase();
		createResponseSql(response, workbook);
	}

	public void procesarFile2Partidas(ResponseModel response)
			throws IOException, SQLException, ServletException, ClassNotFoundException {
		Workbook workbook = loadExcel();
		workbook.removeSheetAt(0);
		workbook.forEach(hoja -> {
			try {
				procesarPartidas(hoja);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		crearDataOnDbPartidas();
		createResponseSql(response, workbook);
	}
	
	public void crearPartidas(ResponseModel response) 
			throws IOException, SQLException, ServletException, ClassNotFoundException {
		getDataItems("PARTIDAS");
		getDataItems("MATERIALES");
		getDataItems("EQUIPOS");
		getDataItems("MANO_OBRA");
		
		Workbook workbook = loadExcelPlantilla();
		
		// Ordenar por partida
		Map<String, List<PartidasDto>> lstPartidasGroupedTemp = lstPartidas.stream()
				  .collect(Collectors.groupingBy(PartidasDto::getId));
		
		Map<String, List<PartidasDto>> lstPartidasGrouped = new TreeMap<String, List<PartidasDto>>(new StringNumberComparator());
		lstPartidasGrouped.putAll(lstPartidasGroupedTemp);
		
		Iterator partidasIterator = lstPartidasGrouped.entrySet().iterator(); 
		while (partidasIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) partidasIterator.next();
            String nombrePartida = (String) mapElement.getKey();
            
            System.out.println(nombrePartida);
            
            List<PartidasDto> partidaItems = (List<PartidasDto>) mapElement.getValue();
            Sheet baseSheet = workbook.cloneSheet(0);
            workbook.setSheetName(workbook.getSheetIndex(baseSheet), nombrePartida);
            Sheet partidaSheet = workbook.getSheet(nombrePartida);
            
            Map<String, List<PartidasDto>> tablasPartidas = partidaItems.stream().collect(Collectors.groupingBy(PartidasDto::getTabla));
            
            String cantidadPartida = partidaItems.get(0).getCantidadPartida();
            String descripcion = partidaItems.get(0).getDescripcion();
            String nroPartida = partidaItems.get(0).getNroPartida();
            String rendimiento = partidaItems.get(0).getRendimiento();
            String unidadRendimiento = partidaItems.get(0).getUnidadRendimiento();
            String unidadPartida = partidaItems.get(0).getUnidadPartida();
            
         // mapeo materiales
            int inicioTablaMateriales = 11;
            if (tablasPartidas.get("MATERIALES") != null) {
            	for (PartidasDto item : tablasPartidas.get("MATERIALES")) {
                	Row row = partidaSheet.getRow(inicioTablaMateriales);
                	String idItem = item.getIdItem();
                	MaterialesDto itemMateriales = this.lstMateriales.stream().filter(material -> material.getCo().equalsIgnoreCase(idItem)).findFirst().get();
                	row.forEach(cell -> {
                		
                		switch (cell.getColumnIndex()) {
    					case 0:
    						cell.setCellValue(idItem);
    						break;
    					case 1:
    						cell.setCellValue(itemMateriales.getNombre());
    						break;
    					case 3:
    						cell.setCellValue(item.getCantidad());
    						break;
    					case 4:
    						cell.setCellValue(itemMateriales.getPunit());
    						break;
    					case 5:
    						cell.setCellValue(item.getPorcentajePerdidas());
    						break;
    					default:
    						break;
    					}
                	});
                	inicioTablaMateriales++;
                };
            }
            
            // mapeo equipos
            int inicioTablaEquipos = 30;
            if (tablasPartidas.get("EQUIPOS") != null) {
            	for (PartidasDto item : tablasPartidas.get("EQUIPOS")) {
                	Row row = partidaSheet.getRow(inicioTablaEquipos);
                	String idItem = item.getIdItem();
                	Optional<EquiposDto> itemEquipoOp = Optional.ofNullable(null);
                	for (EquiposDto equipo : this.lstEquipos) {
                		if (idItem != null && equipo.getCo() != null &&
                				idItem.equalsIgnoreCase(equipo.getCo())) {
                			itemEquipoOp = Optional.of(equipo);
                			break;
                		}
                	}
                	EquiposDto itemEquipo = itemEquipoOp.isPresent() ? itemEquipoOp.get() : new EquiposDto();
                	if (!itemEquipoOp.isPresent()) {
                		System.out.println(idItem);
                	}
                	
                	row.forEach(cell -> {
                		
                		switch (cell.getColumnIndex()) {
    					case 0:
    						cell.setCellValue(idItem);
    						break;
    					case 1:
    						cell.setCellValue(itemEquipo.getNombre());
    						break;
    					case 3:
    						cell.setCellValue(item.getCantidad());
    						break;
    					case 4:
    						cell.setCellValue(itemEquipo.getPunit());
    						break;
    					case 5:
    						cell.setCellValue(item.getPorcentajePerdidas());
    						break;
    					default:
    						break;
    					}
                	});
                	inicioTablaEquipos++;
                };
            }
            
            
            // mapeo de mano
            int inicioTablaMano = 48;
            if (tablasPartidas.get("MANO") != null) {
            	for (PartidasDto item : tablasPartidas.get("MANO")) {
                	Row row = partidaSheet.getRow(inicioTablaMano);
                	String idItem = item.getIdItem();
                	ManoDto itemMano = this.lstManos.stream().filter(mano -> mano.getCo().equalsIgnoreCase(idItem)).findFirst().get();
                	row.forEach(cell -> {
                		
                		switch (cell.getColumnIndex()) {
    					case 0:
    						cell.setCellValue(idItem);
    						break;
    					case 1:
    						cell.setCellValue(itemMano.getNombre());
    						break;
    					case 3:
    						cell.setCellValue(item.getCantidad());
    						break;
    					case 4:
    						cell.setCellValue(itemMano.getPunit());
    						break;
    					default:
    						break;
    					}
                	});
                	inicioTablaMano++;
                };
            }
            
            
            partidaSheet.forEach(row -> {
            	row.forEach(cell -> {
        			String cellValue = dataFormatter.formatCellValue(cell);
        			switch (cellValue) {
					case "{ID_PLANTILLA}":
						cell.setCellValue(nroPartida);
						break;
					case "{DESCRIPCION}":
						cell.setCellValue(descripcion);
						break;
					case "{UNIDAD_RENIDMIENTO}":
						cell.setCellValue(unidadRendimiento);
						break;
					case "{CANTIDAD_RENDIMIENTO}":
						cell.setCellValue(cantidadPartida);
						break;
					case "{RENDIMIENTO}":
						cell.setCellValue(rendimiento);
						break;
					case "{UNIDAD_PARTIDA}":
						cell.setCellValue(unidadPartida);
						break;
					default:
						break;
					}
        		});
        	});
            
        }
		XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
		System.out.println("Creando Archivo");
		 //save file
        FileOutputStream out = new FileOutputStream(ARCHIVO_RESULTANTE);
        workbook.write(out);
        out.close();
        System.out.println("Archivo Creado");
	}
	

	private void procesarPartidas(Sheet sheet) throws ClassNotFoundException, ServletException {
		System.out.println("Nueva hoja");
		
		getDataItems("MATERIALES");
		getDataItems("EQUIPOS");
		getDataItems("MANO_OBRA");
		
		System.out.println("Data cargada");

		boolean encabezado = false;
		boolean materialesTable = false;
		boolean equiposTable = false;
		boolean manoTable = false;

		boolean nroPartidaTable = false;
		boolean descripcionTable = false;

		// set nombre de la partida
		String id = sheet.getSheetName();
		if (id.contains(" - ")) {
			id = id.replace(" - ", " ");
		} else if (id.contains("-")) {
			id = id.replace("-", " ");
		}
		if (id.contains("  ")) {
			id = id.replace("  ", " ");
		}
		String cantidadPartida = null;
		String rendimiento = null;
		String unidadRendimiento = null;
		String tabla = null;
		String nroPartida = null;
		String descripcion = null;
		String unidadPartida = null;

		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			PartidasDto partida = new PartidasDto();
			partida.setId(id);
			partida.setCantidadPartida(cantidadPartida);
			partida.setRendimiento(rendimiento);
			partida.setUnidadRendimiento(unidadRendimiento);
			partida.setTabla(tabla);
			partida.setNroPartida(nroPartida);
			partida.setDescripcion(descripcion);
			partida.setUnidadPartida(unidadPartida);

			boolean valido = false;

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int i = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String cellValue = dataFormatter.formatCellValue(cell);

				// Encabezado
				if (encabezado) {
					if (i == 2) {
						unidadPartida = cellValue;
					}
					if (i == 4) {
						cantidadPartida = cellValue;
					}
					if (i == 5) {
						rendimiento = cellValue;
					}
					if (i == 6) {
						unidadRendimiento = cellValue;
						encabezado = false;
						break;
					}
				}
				// Nro partida
				if (i == 5 && cellValue.contains("Partida N")) {
					nroPartidaTable = true;
				} else if (nroPartidaTable) {
					nroPartida = cellValue;
					nroPartidaTable = false;
				}
				// Descripcion
				if (i == 0 && cellValue.contains("Partida:")) {
					descripcionTable = true;
				} else if (descripcionTable) {
					descripcion = cellValue;
					descripcionTable = false;
				}

				if (i == 0) {
					// Data de la partida
					if (cellValue.contains("Covenin")) {
						encabezado = true;
						break;
					}
					// Data de Materiales
					else if (cellValue.contains("MATERIALES")) {
						materialesTable = true;
						tabla = "MATERIALES";
						break;
					} else if (materialesTable) {
//						partida.setIdItem(cellValue);
					}
					// Data de equipos
					else if (cellValue.contains("EQUIPOS")) {
						equiposTable = true;
						tabla = "EQUIPOS";
						break;
					} else if (equiposTable) {
//						partida.setIdItem(cellValue);
					}
					// Data de equipos
					else if (cellValue.contains("MANO DE OBRA")) {
						manoTable = true;
						tabla = "MANO";
						break;
					} else if (manoTable) {
//						partida.setIdItem(cellValue);
					}
				}
				// Materiales
				else if (materialesTable && i == 1) {
					if (materialesTable && cellValue.equals("")) {
						materialesTable = false;
					} else if (!cellValue.equals("Descripción")) {
						valido = true;
						for (MaterialesDto item : this.lstMateriales) {
							if (item.getNombre().equalsIgnoreCase(cellValue)) {
								partida.setUnidad(item.getUnidad());
								partida.setIdItem(item.getCo());
								partida.setPrecio(item.getPunit());
								break;
							}
						}
					}
//				} else if (materialesTable && i == 2) {
//					partida.setUnidad(cellValue);
				} else if (materialesTable && i == 3) {
					partida.setCantidad(cellValue);
				} else if (materialesTable && i == 5) {
					partida.setPorcentajePerdidas(cellValue != null && cellValue != "" ? cellValue : "0");
				}
				// Equipos
				else if (equiposTable && i == 1) {
					if (equiposTable && cellValue.equals("")) {
						equiposTable = false;
					} else if (!cellValue.equals("Descripción")) {
						valido = true;
						for (EquiposDto item : this.lstEquipos) {
							if (item.getNombre().equalsIgnoreCase(cellValue)) {
								partida.setUnidad(item.getUnidad());
								partida.setIdItem(item.getCo());
								partida.setPrecio(item.getPunit());
								break;
							}
						}
					}
//				} else if (equiposTable && i == 2) {
//					partida.setUnidad(cellValue);
				} else if (equiposTable && i == 3) {
					partida.setCantidad(cellValue);
				} else if (equiposTable && i == 5) {
					partida.setPorcentajePerdidas(cellValue != null && cellValue != null ? cellValue : "1");
				}
				// Mano de obra
				else if (manoTable && i == 1) {
					if (manoTable && cellValue.equals("")) {
						manoTable = false;
					} else if (!cellValue.equals("Descripción")) {
						valido = true;
						for (ManoDto item : this.lstManos) {
							if (item.getNombre().equalsIgnoreCase(cellValue)) {
								partida.setUnidad(item.getUnidad());
								partida.setIdItem(item.getCo());
								partida.setPrecio(item.getPunit());
								break;
							}
						}
					}
//				} else if (manoTable && i == 2) {
//					partida.setUnidad(cellValue);
				} else if (manoTable && i == 3) {
					partida.setCantidad(cellValue);
				}
				i++;
			}
			if (valido) {
				lstPartidas.add(partida);
			}

		}
	}

	private void procesarHojaPartida(Sheet sheet) {
		boolean materialesTable = false;
		boolean equiposTable = false;
		boolean manoTable = false;
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int i = 0;
			String keyMateriales = null;
			String keyEquipos = null;
			String keyMano = null;
			MaterialesDto material = new MaterialesDto();
			EquiposDto equipo = new EquiposDto();
			ManoDto mano = new ManoDto();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String cellValue = dataFormatter.formatCellValue(cell);
				if (i == 0) {
					// Materiales
					if (cellValue.contains("MATERIALES")) {
						materialesTable = true;
						break;
					} else if (materialesTable) {
						material.setId(cellValue);
						keyMateriales = cellValue;
					}
					// Equipos
					else if (cellValue.contains("EQUIPOS")) {
						equiposTable = true;
						break;
					} else if (equiposTable) {
						equipo.setId(cellValue);
						keyEquipos = cellValue;
					}
					// Mano
					else if (cellValue.contains("MANO DE OBRA")) {
						manoTable = true;
						break;
					} else if (manoTable) {
						mano.setId(cellValue);
						keyMano = cellValue;
					}
				}
				// Materiales
				else if (materialesTable && i == 1) {
					if (materialesTable && cellValue.equals("")) {
						materialesTable = false;
					} else {
						keyMateriales = cellValue;
					}
				} else if (materialesTable && i == 2) {
					material.setUnidad(cellValue.equals("") ? null : cellValue);
				}
				// Equipos
				else if (equiposTable && i == 1) {
					if (equiposTable && cellValue.equals("")) {
						equiposTable = false;
					} else {
						keyEquipos = cellValue;
					}
				} else if (equiposTable && i == 2) {
					equipo.setUnidad(cellValue.equals("") ? null : cellValue);
				}
				// Mano
				else if (manoTable && i == 1) {
					if (manoTable && cellValue.equals("")) {
						manoTable = false;
					} else {
						keyMano = cellValue;
					}
				} else if (manoTable && i == 2) {
					mano.setUnidad(cellValue.equals("") ? null : cellValue);
				}
				i++;
			}
			if (keyMateriales != null) {
				hashMateriales.put(keyMateriales, material);
			}
			if (keyEquipos != null) {
				hashEquipos.put(keyEquipos, equipo);
			}
			if (keyMano != null) {
				hashManos.put(keyMano, mano);
			}
		}
	}

	public void getDataItems(String tabla) throws ServletException, ClassNotFoundException {
		HashMap<String, String> respuesta = new HashMap<String, String>();
		try (Connection conn = utils.createDbConnection()) {
			String query = SQL_SELECT_ITEM_BY_NAME.replace("{tabla}", tabla);
			PreparedStatement item = conn.prepareStatement(query);
			ResultSet rs = item.executeQuery();
			while (rs.next()) {
				switch (tabla) {
				case "MATERIALES":
					this.lstMateriales.add(new MaterialesDto(rs.getString("id"), rs.getString("unidad"),
							rs.getString("nombre"), rs.getString("punit"), rs.getString("co"), null));
					break;
				case "EQUIPOS":
					this.lstEquipos.add(new EquiposDto(rs.getString("id"), rs.getString("unidad"),
							rs.getString("nombre"), rs.getString("punit"), rs.getString("co"), null));
					break;
				case "MANO_OBRA":
					this.lstManos.add(new ManoDto(rs.getString("id"), rs.getString("unidad"), rs.getString("nombre"),
							rs.getString("punit"), rs.getString("co")));
					break;
				case "PARTIDAS":
					this.lstPartidas.add(new PartidasDto(rs.getString("id"),
							rs.getString("tabla"), rs.getString("id_item"), rs.getString("cantidad"),
							null, rs.getString("descripcion"), rs.getString("nro_partida"),
							rs.getString("unidad"), rs.getString("cantidad_partida"),
							rs.getString("rendimiento"), rs.getString("unidad_rendimiento"), null,
							rs.getString("porcentaje"), rs.getString("unidad_partida")));
					break;
				default:
					break;
				}
			}

		} catch (SQLException ex) {
			throw new ServletException("Unable to successfully connect to the database. Please check the "
					+ "steps in the README and try again.", ex);
		}
	}

	public void crearDataOnDbPartidas() throws ServletException, ClassNotFoundException {
		try (Connection conn = utils.createDbConnection()) {
			PreparedStatement psPartidas = conn.prepareStatement(SQL_INSERT_PARTIDA);
			lstPartidas.forEach(partidas -> {
				try {
					psPartidas.setString(1, partidas.getId());
					psPartidas.setString(2, partidas.getTabla());
					psPartidas.setString(3, partidas.getIdItem());
					psPartidas.setString(4, partidas.getCantidad());
					psPartidas.setString(5, partidas.getDescripcion());
					psPartidas.setString(6, partidas.getNroPartida());
					psPartidas.setString(7, partidas.getUnidad());
					psPartidas.setString(8, partidas.getCantidadPartida());
					psPartidas.setString(9, partidas.getRendimiento());
					psPartidas.setString(10, partidas.getUnidadRendimiento());
					psPartidas.setString(11, partidas.getPorcentajePerdidas());
					psPartidas.setString(12, partidas.getUnidadPartida());
					psPartidas.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			psPartidas.executeBatch();
		} catch (SQLException ex) {
			throw new ServletException("Unable to successfully connect to the database. Please check the "
					+ "steps in the README and try again.", ex);
		}
	}

	public void crearDataOnDbBase() throws ServletException, ClassNotFoundException {
		try (Connection conn = utils.createDbConnection()) {
			PreparedStatement psMateriales = conn.prepareStatement(SQL_INSERT_MATERIALES);
			hashMateriales.forEach((key, material) -> {
				try {
					psMateriales.setString(1, material.getId());
					psMateriales.setString(2, key);
					psMateriales.setString(3, material.getUnidad());
					psMateriales.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			psMateriales.executeBatch();

			PreparedStatement psEquipos = conn.prepareStatement(SQL_INSERT_EQUIPOS);
			hashEquipos.forEach((key, equipo) -> {
				try {
					psEquipos.setString(1, equipo.getId());
					psEquipos.setString(2, key);
					psEquipos.setString(3, equipo.getUnidad());
					psEquipos.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			psEquipos.executeBatch();

			PreparedStatement psManos = conn.prepareStatement(SQL_INSERT_MANOS);
			hashManos.forEach((key, mano) -> {
				try {
					psManos.setString(1, mano.getId());
					psManos.setString(2, key);
					psManos.setString(3, mano.getUnidad());
					psManos.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			psManos.executeBatch();
		} catch (SQLException ex) {
			throw new ServletException("Unable to successfully connect to the database. Please check the "
					+ "steps in the README and try again.", ex);
		}
	}

	public Workbook loadExcel() throws EncryptedDocumentException, IOException {
		byte[] data;
		data = utils.loadFileFromLocal("C:\\Users\\pedro\\OneDrive\\Documents\\partidas\\", "20101-ID00GD08001.xls");
		return WorkbookFactory.create(new ByteArrayInputStream(data));
	}
	
	public Workbook loadExcelPlantilla() throws EncryptedDocumentException, IOException {
		byte[] data;
		data = utils.loadFileFromLocal("C:\\Users\\pedro\\OneDrive\\Documents\\partidas\\", "PLANTILLA.xlsx");
		return WorkbookFactory.create(new ByteArrayInputStream(data));
	}

	public void createResponse(HttpServletResponse response, Workbook workbook) throws IOException {
		response.getWriter().write("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
		response.getWriter().flush();
		response.getWriter().close();
	}

	public void createResponseSql(ResponseModel response, Workbook workbook) throws IOException {
		response.setResultado(new ObjectMapper().writeValueAsString(lstMateriales));
	}

}