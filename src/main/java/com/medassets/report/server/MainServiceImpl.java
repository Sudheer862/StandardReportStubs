package com.medassets.report.server;

import com.avega.util.SpringBeanLocator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.medassets.report.client.service.MainService;
import com.medassets.report.library.dao.ReportLibraryDAO;
import com.medassets.report.library.domain.CategoryDTO;
import com.medassets.report.library.domain.FolderDTO;
import com.medassets.report.library.domain.ReportDTO;
import com.medassets.report.shared.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class MainServiceImpl extends RemoteServiceServlet implements
		MainService {

	private static final long serialVersionUID = -7572017972759103053L;
	private static final Logger LOGGER = Logger.getLogger(MainServiceImpl.class
			.getName());
	private static final boolean USE_MOCK_DATA = true;

	private static ReportLibraryDAO libraryDAO = null;
	static {
		if (!USE_MOCK_DATA) {
			libraryDAO = (ReportLibraryDAO) SpringBeanLocator
					.getBean("reportLibraryDAO");
		}
	}

	public List<TreeItemDTO> getAllTreeData() {
		List<TreeItemDTO> list = new ArrayList<TreeItemDTO>();
		List<TreeItemDTO> cats = getCategories();
		if (cats != null && cats.size() > 0) {
			cats.get(1).setType(1);
		}
		list.addAll(cats);
		list.add(getTopNodeFolder());
		return list;
	}

	/**
	 * Retrieve all reports (template and instance) from the ReportLibraryDAO.
	 * 
	 * @return
	 */
	public List<ReportItemDTO> getReports() {
		List<ReportItemDTO> list = new ArrayList<ReportItemDTO>();
		if (USE_MOCK_DATA) {
			list.addAll(getMockReports());
			// list.addAll(getMockTemplates());
		} else {
			list.addAll(getDAOReports());
		}
		return list;
	}

	/*
	 * private List<ReportItemDTO> getMockTemplates(){ List<ReportItemDTO>
	 * reports = new ArrayList<ReportItemDTO>();
	 * 
	 * for (int i = 0; i < 500; i++) { ReportDTO dto = new ReportDTO();
	 * dto.setCategory("Consumers and Encounters");
	 * 
	 * dto.setFolderName("Consumers and Encounters"); dto.setName("Report " +
	 * Math.random()); if (i % 2 == 0) { dto.setReportType("Report"); } else {
	 * dto.setReportType("Report Template"); }
	 * dto.setTemplateName("Template Name"); dto.setReportTemplateId(0L);
	 * dto.setCreatedCode("Bubba"); dto.setCreatedDate(new Date());
	 * dto.setModifiedCode("Smith"); dto.setModifiedDate(new Date());
	 * reports.add(toDTO(dto)); } return reports;
	 * 
	 * }
	 */

	/**
	 * Retrieve the top node folder (and all it's children) from the
	 * ReportLibraryDAO.
	 * 
	 * @return
	 */
	public TreeItemDTO getTopNodeFolder() {

		TreeItemDTO dto = null;
		if (USE_MOCK_DATA) {
			dto = toDTO(getMockFolder());
		} else {
			dto = toDTO(libraryDAO.getTopNodeFolder());
		}
		return dto;
	}

	public List<TreeItemDTO> getCategories() {
		List<TreeItemDTO> list = new ArrayList<TreeItemDTO>();
		if (USE_MOCK_DATA) {
			list = getMockCategories();
		} else {
			List<CategoryDTO> dtos = libraryDAO.getCategories();
			for (CategoryDTO dto : dtos) {
				list.add(toDTO(dto));
			}
		}
		return list;
	}

	private List<TreeItemDTO> getMockCategories() {
		List<TreeItemDTO> cats = new ArrayList<TreeItemDTO>();

		CategoryItemDTO templates = new CategoryItemDTO("Templates");
		CategoryItemDTO ConsumersandEncounters = new CategoryItemDTO(
				"Consumers and Encounters");
		List<CategoryItemDTO> children = new ArrayList<CategoryItemDTO>();
		children.add(new CategoryItemDTO("Consumers and Encounters"));
		children.add(new CategoryItemDTO("Contract and A/R Management"));
		children.add(new CategoryItemDTO("Costing"));
		children.add(new CategoryItemDTO("Flexible Reports"));
		children.add(new CategoryItemDTO("General Reports"));
		children.add(new CategoryItemDTO("Profitability and Utilization"));
		templates.setChildren(children);
		cats.add(toDTO(templates, "Template"));

		CategoryItemDTO reports = new CategoryItemDTO("Reports");
		children = new ArrayList<CategoryItemDTO>();
		children.add(new CategoryItemDTO("Consumers and Encounters"));
		children.add(new CategoryItemDTO("Contract and A/R Management"));
		children.add(new CategoryItemDTO("Costing"));
		children.add(new CategoryItemDTO("Flexible Reports"));
		children.add(new CategoryItemDTO("General Reports"));
		children.add(new CategoryItemDTO("Profitability and Utilization"));
		reports.setChildren(children);
		cats.add(toDTO(reports, "Instance"));

		return cats;
	}

	private FolderDTO getMockFolder() {
		FolderDTO folder = new FolderDTO();
		folder.setName("Folders");
		folder.setParentFolderId(0L);
		folder.setObjectid(1L);

		List<FolderDTO> children = new ArrayList<FolderDTO>();

		FolderDTO dto = new FolderDTO();
		dto.setName("Andrew");
		dto.setParentFolderId(1L);
		children.add(dto);

		dto = new FolderDTO();
		dto.setName("General");
		dto.setParentFolderId(1L);
		children.add(dto);

		dto = new FolderDTO();
		dto.setName("Joseph");
		dto.setParentFolderId(1L);
		children.add(dto);

		dto = new FolderDTO();
		dto.setName("Mike");
		dto.setParentFolderId(1L);
		children.add(dto);

		dto = new FolderDTO();
		dto.setName("Susan");
		dto.setParentFolderId(1L);
		children.add(dto);

		dto = new FolderDTO();
		dto.setName("Tom");
		dto.setParentFolderId(1L);
		children.add(dto);

		folder.setChildren(children);
		return folder;
	}

	private List<ReportItemDTO> getDAOReports() {
		List<ReportItemDTO> list = new ArrayList<ReportItemDTO>();
		for (ReportDTO report : libraryDAO.getReports()) {
			list.add(toDTO(report));
		}
		return list;
	}

	private List<ReportItemDTO> getMockReports() {
		List<ReportItemDTO> reports = new ArrayList<ReportItemDTO>();
		for (int j = 0; j < 6; j++) {

			for (int i = 0; i < 15; i++) {
				ReportDTO dto = new ReportDTO();
				if (j == 0)
					dto.setCategory("Consumers and Encounters");
				if (j == 1)
					dto.setCategory("Contract and A/R Management");
				if (j == 2)
					dto.setCategory("Costing");
				if (j == 3)
					dto.setCategory("Flexible Reports");
				if (j == 4)
					dto.setCategory("General Reports");
				if (j == 5)
					dto.setCategory("Profitability and Utilization");

				dto.setFolderName("Tom");
				if (i == 0)
					dto.setName("Consumer Information");
				if (i == 1)
					dto.setName("encounter by Consumer");
				if (i == 2)
					dto
							.setName("encounters:Ambulatory paymnent classification");
				if (i == 3)
					dto.setName("encounters:Chaargers");
				if (i == 4)
					dto.setName("encounters:ClinicalProcedures");
				if (i == 5)
					dto.setName("encounters:diogonois");
				if (i == 6)
					dto.setName("encounters:DRG");
				if (i == 7)
					dto.setName("encounters:Finanacial summary");
				if (i == 8)
					dto.setName("encounters:General");
				if (i == 9)
					dto.setName("encounters:Payment/Transanction summary");
				if (i == 10)
					dto.setName("encounters:Payments");
				if (i == 11)
					dto.setName("encounters:Practionaors");
				if (i == 12)
					dto.setName("encounters:Profile");
				if (i == 13)
					dto.setName("encounter:services");
				if (i == 14)
					dto.setName("Resource Utilization profile");

				dto.setReportType("Report Template");

				dto.setTemplateName("Template Name");
				dto.setReportTemplateId((long) j);
				// dto.setReportInstanceId((long)i);
				dto.setCreatedCode("Bubba");
				dto.setCreatedDate(new Date());
				dto.setModifiedCode("Smith");
				dto.setModifiedDate(new Date());

				reports.add(toDTO(dto));

			}
		}

		for (int i = 0; i < StubStandardServiceImpl.reports.length; i++)
			reports.add(toDTO(StubStandardServiceImpl.reports[i]));

		return reports;
	}

	/**
	 * Convert a CategoryDTO to a TreeItemDTO. All children will be converted as
	 * well.
	 * 
	 * @param folder
	 * @return
	 */
	private TreeItemDTO toDTO(CategoryDTO cat) {
		if (cat == null) {
			return null;
		}
		TreeItemDTO dto = new TreeItemDTO(cat.getDescription(), 0);
		dto.setCategory(cat.getCode());
		List<TreeItemDTO> children = new ArrayList<TreeItemDTO>();
		if (cat.getChildCategories() != null) {
			for (CategoryDTO child : cat.getChildCategories()) {
				TreeItemDTO ti = toDTO(child);
				children.add(ti);
			}
			dto.setChildren(children);
		}
		return dto;
	}

	/**
	 * Convert a FolderDTO to a TreeItemDTO. All children will be converted as
	 * well.
	 * 
	 * @param folder
	 * @return
	 */
	private TreeItemDTO toDTO(FolderDTO folder) {
		if (folder == null) {
			return null;
		}
		TreeItemDTO dto = new TreeItemDTO(folder.getName(), 2);
		dto.setObjectId(folder.getObjectid());
		dto.setParentFolderId(folder.getParentFolderId());
		List<TreeItemDTO> children = new ArrayList<TreeItemDTO>();
		for (FolderDTO child : folder.getChildren()) {
			TreeItemDTO ti = toDTO(child);
			children.add(ti);
		}
		dto.setChildren(children);
		return dto;
	}

	/**
	 * Convert a TreeItemDTO to a FolderDTO. All children will be converted as
	 * well.
	 * 
	 * @param treeItem
	 * @return
	 */
	private FolderDTO toDTO(TreeItemDTO treeItem) {
		if (treeItem == null) {
			return null;
		}

		FolderDTO dto = new FolderDTO();
		dto.setParentFolderId(treeItem.getParentFolderId());
		dto.setObjectid(treeItem.getObjectId());
		dto.setName(treeItem.getName());
		List<FolderDTO> children = new ArrayList<FolderDTO>();
		for (TreeItemDTO child : treeItem.getChildren()) {
			FolderDTO ti = toDTO(child);
			children.add(ti);
		}
		dto.setChildren(children);
		return dto;
	}

	/**
	 * Convert a ReportDTO to a ReportItemDTO.
	 * 
	 * @param report
	 * @return
	 */
	private ReportItemDTO toDTO(ReportDTO report) {
		if (report == null) {
			return null;
		}
		ReportItemDTO dto = new ReportItemDTO();
		dto.setName(report.getName());
		dto.setCategory(report.getCategory());
		dto.setCreatedCode(report.getCreatedCode());
		dto.setCreatedDate(report.getCreatedDate() != null ? report
				.getCreatedDate().getTime() : 0);
		dto.setDescription(report.getDescription());
		dto.setModifiedCode(report.getModifiedCode());
		dto.setModifiedDate(report.getModifiedDate() != null ? report
				.getModifiedDate().getTime() : 0);
		dto.setFolderName(report.getFolderName());
		dto.setReportInstanceId(report.getReportInstanceId());
		dto.setReportTemplateId(report.getReportTemplateId());
		dto.setReportType(report.getReportType());
		dto.setTemplateName(report.getTemplateName());
		dto.setPublished(report.getPublished());
		dto.setFolderId(report.getFolderId());
		dto.setSubTitle(report.getSubTitleName());
		return dto;
	}

	/**
	 * Convert a ReportItemDTO to a ReportDTO.
	 * 
	 * @param reportItem
	 * @return
	 */
	private ReportDTO toDTO(ReportItemDTO reportItem) {
		if (reportItem == null) {
			return null;
		}
		ReportDTO dto = new ReportDTO();
		dto.setName(reportItem.getName());
		dto.setCategory(reportItem.getCategory());
		dto.setCreatedCode(reportItem.getCreatedCode());
		dto.setCreatedDate(reportItem.getCreatedDate() != null ? new Date(
				reportItem.getCreatedDate()) : null);
		dto.setDescription(reportItem.getDescription());
		dto.setModifiedCode(reportItem.getModifiedCode());
		dto.setModifiedDate((reportItem.getModifiedDate() != null ? new Date(
				reportItem.getModifiedDate()) : null));
		dto.setFolderName(reportItem.getFolderName());
		dto.setReportInstanceId(reportItem.getReportInstanceId());
		dto.setReportTemplateId(reportItem.getReportTemplateId());
		dto.setReportType(reportItem.getReportType());
		dto.setTemplateName(reportItem.getTemplateName());
		dto.setPublished(reportItem.getPublished());
		dto.setFolderId(reportItem.getFolderId());
		dto.setSubTitleName(reportItem.getSubTitle());
		return dto;
	}

	private TreeItemDTO toDTO(CategoryItemDTO cat, String type) {
		if (cat == null) {
			return null;
		}
		TreeItemDTO dto = new TreeItemDTO();
		dto.setCategory(type);
		dto.setName(cat.getCode());
		if (cat.getChildren() != null) {
			List<TreeItemDTO> children = new ArrayList<TreeItemDTO>();
			for (CategoryItemDTO child : cat.getChildren()) {
				children.add(toDTO(child, child.getCode()));
			}
			dto.setChildren(children);
		}
		return dto;
	}

	public String deleteReport(ReportItemDTO report) {

		// convert to report DTO
		ReportDTO dto = null;
		dto = toDTO(report);
		libraryDAO.deleteReport(dto);

		// TODO Auto-generated method stub

		System.out.println("Report Delete: ReportDescription ="
				+ report.getName());
		return null;
	}

	public String deleteFolder(TreeItemDTO folder) {
		// convert to report DTO
		// TreeDTO dto = null;
		// dto = toDTO(report);
		// libraryDAO.deleteReport(dto);
		FolderDTO dto;
		dto = toDTO(folder);
		libraryDAO.deleteFolder(dto);
		// TODO Auto-generated method stub
		System.out.println("Report Delete: ReportDescription ="
				+ folder.getName());
		return null;
	}

	public void renameFolder(TreeItemDTO folder) {
		FolderDTO dto = toDTO(folder);
		libraryDAO.updateFolder(dto);
	}

	public String getUserDisplayName() {
		return libraryDAO.getUserDisplayName();
	}

	public String newFolder(TreeItemDTO parentFolder, String folderName) {

		FolderDTO dto = new FolderDTO();
		dto.setName(folderName);
		FolderDTO parent = toDTO(parentFolder);
		dto.setParentFolderId(parent.getObjectid());
		libraryDAO.insertFolder(dto);

		return null;
	}
}