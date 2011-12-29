package com.medassets.report.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.avega.security.SecurityManager;
import com.avega.util.SpringBeanLocator;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.medassets.report.client.service.StandardService;
import com.medassets.report.crystal.common.CrystalViewerSupportDTO;
import com.medassets.report.crystal.viewer.CrystalReportViewerService;
import com.medassets.report.library.dao.ReportLibraryDAO;
import com.medassets.report.shared.ParamAvailableItemValue;
import com.medassets.report.shared.ParamAvailableItems;
import com.medassets.report.shared.ReportItemDTO;
import com.medassets.report.shared.StandardParamItemDTO;
import com.medassets.report.shared.StandardRptInstanceSupportItemDTO;
import com.medassets.report.standard.dao.ReportInstanceParamDAO;
import com.medassets.report.standard.dao.ReportParamDAO;
import com.medassets.report.standard.domain.DataTypeEnum;
import com.medassets.report.standard.domain.ParamAvailableValue;
import com.medassets.report.standard.domain.StandardParamDTO;
import com.medassets.report.standard.domain.StandardRptInstanceSupportDTO;
import com.medassets.report.standard.domain.SubParam;
import com.medassets.report.standard.domain.WizStyleEnum;

public class StandardServiceImpl extends RemoteServiceServlet implements
		StandardService {

	private static final Logger LOGGER = Logger
			.getLogger(StandardServiceImpl.class.getName());
	private static final boolean USE_MOCK_DATA = true;

	private static ReportParamDAO paramDAO = null;
	private static ReportInstanceParamDAO reportInstanceParamDAO = null;
	private static ReportLibraryDAO libraryDAO = null;

	private static CrystalReportViewerService crystalViewerService = null;

	static {
		if (!USE_MOCK_DATA) {
			paramDAO = (ReportParamDAO) SpringBeanLocator
					.getBean("standardReportParameterDAO");
			reportInstanceParamDAO = (ReportInstanceParamDAO) SpringBeanLocator
					.getBean("standardReportInstanceParameterDAO");
			libraryDAO = (ReportLibraryDAO) SpringBeanLocator
					.getBean("reportLibraryDAO");
			crystalViewerService = (CrystalReportViewerService) SpringBeanLocator
					.getBean("crystalReportViewerService");
		}
	}

	private static final long serialVersionUID = 1L;
	StubStandardServiceImpl stub = new StubStandardServiceImpl();

	public List<StandardParamItemDTO> getStandardParamDTOList(Long reportId,
			boolean isInstance) {

		ArrayList<StandardParamItemDTO> list = new ArrayList<StandardParamItemDTO>();
		if (USE_MOCK_DATA) {
			if (isInstance) {

				return stub.getUpdatedParams(reportId);

			} else {
				return stub.getParams(reportId);
			}
		} else {
			try {
				List<StandardParamDTO> result;
				if (isInstance) {
					LOGGER
							.info("In StandardServiceImpl - Getting Parameter List for report Instance ID  "
									+ reportId);
					result = reportInstanceParamDAO
							.getStandardReportInstanceParamDTOList(reportId);
				} else {
					LOGGER
							.info("In StandardServiceImpl - Getting Parameter List for report Template ID  "
									+ reportId);
					result = paramDAO.getStandardParamDTOList(reportId);
				}
				for (StandardParamDTO dto : result) {
					list.add(toDTO(dto));
				}
			} catch (Exception ex) {
				LOGGER.log(Level.ERROR, ex.getMessage(), ex);
			}
		}
		return list;
	}

	private StandardParamItemDTO toDTO(StandardParamDTO param) {
		StandardParamItemDTO dto = new StandardParamItemDTO();
		StandardParamDTO parentParam;
		StandardParamItemDTO paramItemDTO;
		dto.setDefaultParamCodeValueInCrystalReport(param
				.getDefaultParamCodeValueInCrystalReport());
		dto.setDefaultParamDescriptionInCrystalReport(param
				.getDefaultParamDescriptionInCrystalReport());
		dto.setParamDataType(param.getParamDataType().ordinal());
		dto.setParamID(param.getParamID());
		dto.setParamName(param.getParamName());
		dto.setPromptDescription(param.getPromptDescription());
		dto.setReportInstanceID(param.getReportInstanceID());
		dto.setReportTemplateID(param.getReportTemplateID());
		dto.setSubParam(param.isSubParam());
		dto.setParamSelAll(param.isParamSelAll());
		dto.setParamSQLBased(param.isParamSQLBased());
		dto.setLookup(param.getLookup());
		dto.setaValue(param.getaValue());
		dto.setParentParam(param.isParentParam());
		dto.setParentName(param.getParentParam());
		// Setting modified property to DTO.
		dto.setModified(param.isModified());
		if (param.getSubParam() != null && param.getSubParam().getCount() > 0) {
			parentParam = param.getSubParam().getStandardParamDTO(0);
			paramItemDTO = toDTO(parentParam);
			dto.setParentParam(paramItemDTO);

		}

		switch (param.getWizStyle()) {
		case TEXT: // TEXT, CHK, CALENDAR, LIST, CBO
			dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.TEXT);
			break;
		case CHK:
			dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.CHK);
			break;
		case CALENDAR:
			dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.CALENDAR);
			break;
		case LIST:
			dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.LIST);
			break;
		case CBO:
			dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.CBO);
			break;
		}
		if (dto.getWizStyle().equals(StandardParamItemDTO.WizStyleEnum.LIST)) {
			dto.setaValueList(param.getaValue());
		}
		return dto;
	}

	private StandardParamDTO toDTO(StandardParamItemDTO param) {
		StandardParamDTO dto = new StandardParamDTO();
		dto.setDefaultParamCodeValueInCrystalReport(param
				.getDefaultParamCodeValueInCrystalReport());
		dto.setDefaultParamDescriptionInCrystalReport(param
				.getDefaultParamDescriptionInCrystalReport());
		dto.setParamDataType(DataTypeEnum.values()[param.getParamDataType()]);
		dto.setParamID(param.getParamID());
		dto.setParamName(param.getParamName());
		dto.setPromptDescription(param.getPromptDescription());
		dto.setReportInstanceID(param.getReportInstanceID());
		dto.setReportTemplateID(param.getReportTemplateID());
		dto.setParamSQLBased(param.isParamSQLBased());
		dto.setSubParam(param.isSubParam());
		dto.setParamSelAll(param.isParamSelAll());
		dto.setLookup(param.getLookup());
		dto.setaValue(param.getaValue());
		dto.setParentParam(param.isParentParam());
		dto.setParentParam(param.getParentName());
		// Getting the modified field to know if param needs to be updated or
		// not.
		dto.setModified(param.isModified());
		if (param.getParentParam() != null) {
			StandardParamDTO standardParamDTO = toDTO(param.getParentParam());
			SubParam subParam = new SubParam();
			subParam.add(standardParamDTO);
			dto.setSubParam(subParam);

		}
		switch (param.getWizStyle()) {
		case TEXT: // TEXT, CHK, CALENDAR, LIST, CBO
			dto.setWizStyle(WizStyleEnum.TEXT);
			break;
		case CHK:
			dto.setWizStyle(WizStyleEnum.CHK);
			break;
		case CALENDAR:
			dto.setWizStyle(WizStyleEnum.CALENDAR);
			break;
		case LIST:
			dto.setWizStyle(WizStyleEnum.LIST);
			break;
		case CBO:
			dto.setWizStyle(WizStyleEnum.CBO);
			break;
		}
		return dto;
	}

	public ParamAvailableItems getAvailableValuesForCBOorLIST(
			StandardParamItemDTO dto) {
		StandardParamDTO sDto = toDTO(dto);
		ArrayList<ParamAvailableItemValue> list = new ArrayList<ParamAvailableItemValue>();
		List<ParamAvailableValue> result = new ArrayList<ParamAvailableValue>();
		boolean codeOnly = false;
		boolean allNull = true;
		if (USE_MOCK_DATA) {
			result = new StubStandardServiceImpl()
					.getAvailableValuesForCBOorLIST(sDto);
		} else {
			result = paramDAO.getAvailableValuesForCBOorLIST(sDto);
		}
		try {

			if (("*").equals(dto.getDefaultParamCodeValueInCrystalReport())) {
				ParamAvailableItemValue defaultValue = new ParamAvailableItemValue();
				defaultValue.setCode("*");
				defaultValue.setName("ALL");

				list.add(defaultValue);
			}

			// Temporary testing hack!
			int limit = 1000;

			for (ParamAvailableValue p : result) {
				ParamAvailableItemValue param = toDTO(p);
				if (param.getName() != null) {
					allNull = false;
				}
				list.add(param);
				if (list.size() >= limit) {
					break;
				}
			}
			if (result != null && result.size() > 0) {
				codeOnly = result.get(0).isPrependCodeToDescription();
			}

		} catch (Exception ex) {
			LOGGER.log(Level.ERROR, ex.getMessage(), ex);
		}

		ParamAvailableItems pai = new ParamAvailableItems();
		pai.setCodeOnly(codeOnly && allNull);
		pai.setParams(list);
		return pai;
	}

	private ParamAvailableItemValue toDTO(ParamAvailableValue p) {
		ParamAvailableItemValue s = new ParamAvailableItemValue();
		s.setCode(p.getCode());
		s.setName(p.getDescription());
		return s;
	}

	public void saveStandardReportInstance(String reportInstanceName,
			List<StandardParamItemDTO> standardParamDTOList,
			StandardRptInstanceSupportItemDTO supportDTO) {

		if (USE_MOCK_DATA) {
			StubStandardServiceImpl.saveReport(reportInstanceName,
					standardParamDTOList, supportDTO);

		} else {
			String user = libraryDAO.getUserDisplayName();
			supportDTO.setCreatingUser(user);
			supportDTO.setModifyingUser(user);
			List<StandardParamDTO> list = new ArrayList<StandardParamDTO>();
			for (StandardParamItemDTO dto : standardParamDTOList) {
				list.add(toDTO(dto));
			}
			reportInstanceParamDAO.saveStandardReportInstance(
					reportInstanceName, list, toDTO(supportDTO));
		}
	}

	public void updateStandardReportInstance(
			List<StandardParamItemDTO> standardParamDTOList,
			StandardRptInstanceSupportItemDTO supportDTO) {
		// Getting the current logged user info who modified the instance.
		String user = libraryDAO.getUserDisplayName();
		supportDTO.setModifyingUser(user);
		// Converting UI DTO to back end DTO's
		List<StandardParamDTO> list = new ArrayList<StandardParamDTO>();
		for (StandardParamItemDTO dto : standardParamDTOList) {
			list.add(toDTO(dto));
		}
		StandardRptInstanceSupportDTO instanceSupportDTO = toDTO(supportDTO);
		// Calling the database to update the instance with changes.
		reportInstanceParamDAO.updateStandardReportInstance(instanceSupportDTO,
				list);
	}

	private StandardRptInstanceSupportDTO toDTO(
			StandardRptInstanceSupportItemDTO item) {
		StandardRptInstanceSupportDTO dto = new StandardRptInstanceSupportDTO();
		dto.setFolderID(item.getFolderID());
		dto.setCreatingUser(item.getCreatingUser());
		dto.setModifyingUser(item.getModifyingUser());
		dto.setPublished(item.getPublished());
		dto.setRptInstanceName(item.getRptInstanceName());
		dto.setRptInstanceDescription(item.getRptInstanceDescription());
		dto.setSubTitleName(item.getSubTitleName());
		dto.setTemplateID(item.getTemplateID());
		// Setting the instance id.
		dto.setRptInstanceId(item.getInstanceID());
		return dto;
	}

	public void runStandardReport(ReportItemDTO reportItemDTO,
			List<StandardParamItemDTO> dtoList) {

		voidSessionCloseDocument();
		CrystalViewerSupportDTO crystalViewerSupportDTO = new CrystalViewerSupportDTO();
		crystalViewerSupportDTO.setPassword(SecurityManager.getInstance()
				.getUser().getAdsPassword());
		crystalViewerSupportDTO.setUser(SecurityManager.getInstance().getUser()
				.getAdsUserName());
		crystalViewerSupportDTO.setReportTemplateID(reportItemDTO
				.getReportTemplateId());

		List<StandardParamDTO> parmList = new ArrayList<StandardParamDTO>();
		for (StandardParamItemDTO dto : dtoList) {
			parmList.add(toDTO(dto));
		}

		ReportClientDocument rdc = crystalViewerService.runStandardReport(
				parmList, crystalViewerSupportDTO);

		this.getThreadLocalRequest().getSession()
				.setAttribute("clientdoc", rdc);
	}

	@Override
	public void voidSessionCloseDocument() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		ReportClientDocument rdc = (ReportClientDocument) this
				.getThreadLocalRequest().getSession().getAttribute("clientdoc");
		try {
			if (rdc != null) {
				rdc.close();
			}
			this.getThreadLocalRequest().getSession().removeAttribute(
					"clientdoc");
		} catch (ReportSDKException e) {
			LOGGER.log(Level.ERROR, e.getMessage(), e);
		}

	}
}
