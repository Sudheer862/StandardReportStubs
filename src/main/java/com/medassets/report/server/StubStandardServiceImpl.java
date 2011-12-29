package com.medassets.report.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.medassets.report.library.domain.ReportDTO;
import com.medassets.report.shared.StandardParamItemDTO;
import com.medassets.report.shared.StandardRptInstanceSupportItemDTO;
import com.medassets.report.standard.domain.ParamAvailableValue;
import com.medassets.report.standard.domain.StandardParamDTO;

public class StubStandardServiceImpl {
	List<ParamAvailableValue> result = new ArrayList<ParamAvailableValue>();
	static int c = 0, i = 0, j = 0;
	static ReportDTO[] reports = new ReportDTO[20];
	static Map<Long, List<StandardParamItemDTO>> map = new HashMap<Long, List<StandardParamItemDTO>>();
	static Map<Long, List<StandardParamItemDTO>> updateMap = new HashMap<Long, List<StandardParamItemDTO>>();
	StandardParamItemDTO dto;
	ArrayList<StandardParamItemDTO> list;

	public StubStandardServiceImpl() {
		for (int j = 0; j < 16; j++) {
			list = new ArrayList<StandardParamItemDTO>();
			for (int i = 0; i < 7; i++) {

				dto = new StandardParamItemDTO();
				dto.setPromptDescription("Parameter " + i);
				if (i == 0) {
					dto.setDefaultParamCodeValueInCrystalReport("ALL");
					dto.setPromptDescription("Consumer Id or ALL");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.TEXT);
				}
				if (i == 1) {
					dto.setDefaultParamCodeValueInCrystalReport("ALL");
					dto.setPromptDescription("Zip codes");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.LIST);
					// dto.setaValueList("aValueList");
				}
				if (i == 2) {
					dto.setDefaultParamCodeValueInCrystalReport("0.0");
					dto.setPromptDescription("Age Range");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.TEXT);
				}
				if (i == 3) {
					dto.setDefaultParamCodeValueInCrystalReport("12.0");
					dto.setPromptDescription("to");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.TEXT);
				}
				if (i == 4) {
					dto.setDefaultParamCodeValueInCrystalReport("ALL");
					dto.setPromptDescription("Gender");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.LIST);
					// dto.setaValueList("aValueList");
				}
				if (i == 5) {
					dto.setDefaultParamCodeValueInCrystalReport("ALL");
					dto.setPromptDescription("Ethnicity");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.LIST);
					// dto.setaValueList("aValueList");
				}
				if (i == 6) {
					dto.setDefaultParamCodeValueInCrystalReport("18-May-1987");
					dto.setPromptDescription("Consumer History Refrence Date");
					dto.setWizStyle(StandardParamItemDTO.WizStyleEnum.TEXT);
				}
				dto.setParamSQLBased(true);
				dto.setParamSelAll(true);

				list.add(dto);
			}
			map.put((long) j, list);
		}
	}

	List<StandardParamItemDTO> getUpdatedParams(Long instanceId) {
		return updateMap.get(instanceId);

	}

	List<StandardParamItemDTO> getParams(Long instanceId) {
		return map.get(instanceId);
	}

	public static void saveUpdatedMap(
			List<StandardParamItemDTO> standardParamDTOList) {

		StandardParamItemDTO dto;
		ArrayList<StandardParamItemDTO> list = new ArrayList<StandardParamItemDTO>();
		Iterator<StandardParamItemDTO> set = standardParamDTOList.iterator();

		while (set.hasNext()) {

			dto = set.next();
			dto.setReportInstanceID((long) j);
			list.add(dto);
		}
		updateMap.put((long) j, list);
	}

	public static void saveReport(String reportInstanceName,
			List<StandardParamItemDTO> standardParamDTOList,
			StandardRptInstanceSupportItemDTO supportDTO) {

		saveUpdatedMap(standardParamDTOList);
		System.out.println("Template id" + supportDTO.getTemplateID());
		reports[i] = new ReportDTO();
		if (supportDTO.getTemplateID() == 0)
			reports[i].setCategory("Consumers and Encounters");
		else if (supportDTO.getTemplateID() == 1)
			reports[i].setCategory("Contract and A/R Management");
		else if (supportDTO.getTemplateID() == 2)
			reports[i].setCategory("Costing");
		else if (supportDTO.getTemplateID() == 3)
			reports[i].setCategory("Flexible Reports");
		else if (supportDTO.getTemplateID() == 4)
			reports[i].setCategory("General Reports");
		else
			reports[i].setCategory("Profitability and Utilization");
		if(j==0)
		reports[i].setFolderName("Andrew");
		if(j==1)
			reports[i].setFolderName("General");
		if(j==2)
			reports[i].setFolderName("Joseph");
		if(j==3)
			reports[i].setFolderName("Mike");
		if(j==4)
			reports[i].setFolderName("Susan");
		if(j==5)
			reports[i].setFolderName("Tom");
		reports[i].setName(reportInstanceName);

		reports[i].setReportType("Report");

		reports[i].setTemplateName(reportInstanceName);
		reports[i].setReportTemplateId(supportDTO.getTemplateID());
		reports[i].setReportInstanceId((long) j++);
		reports[i].setCreatedCode("nisum");
		reports[i].setCreatedDate(new Date());

		reports[i].setModifiedCode("Smith");
		reports[i].setModifiedDate(new Date());
		i++;

	}

	public List<ParamAvailableValue> getAvailableValuesForCBOorLIST(
			StandardParamDTO dto) {

		for (int i = 0; i < 11; i++) {
			ParamAvailableValue param = new ParamAvailableValue();

			if (dto.getPromptDescription().equalsIgnoreCase("Zip codes")) {
				param.setCode("ZIP" + i);
				param.setDescription("ZIP Description" + i);
			} else if (dto.getPromptDescription().equalsIgnoreCase("Gender")) {
				param.setCode("GENDER" + i);
				param.setDescription("GENDER Description" + i);
			} else {
				param.setCode("ETHINITY" + i);
				param.setDescription("ETHINICITY Description" + i);
			}
			param.setPrependCodeToDescription(true);
			result.add(param);
		}
		return result;
	}

}
