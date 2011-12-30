package com.medassets.report.client.standard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.medassets.report.client.component.MACheckBox;
import com.medassets.report.client.component.MAComponent;
import com.medassets.report.client.component.MADateBox;
import com.medassets.report.client.component.MAFormPanel;
import com.medassets.report.client.component.MAListBox;
import com.medassets.report.client.component.MASelector;
import com.medassets.report.client.component.MATextBox;
import com.medassets.report.client.library.Main;
import com.medassets.report.client.service.StandardService;
import com.medassets.report.client.service.StandardServiceAsync;
import com.medassets.report.shared.ParamAvailableItemValue;
import com.medassets.report.shared.ParamAvailableItems;
import com.medassets.report.shared.ReportItemDTO;
import com.medassets.report.shared.StandardParamItemDTO;
import com.medassets.report.shared.StandardRptInstanceSupportItemDTO;
import com.medassets.report.shared.StandardParamItemDTO.WizStyleEnum;

public class StandardReportDialog extends FlowPanel implements ResizeHandler {

    private static StandardReportDialogUiBinder uiBinder = GWT.create(StandardReportDialogUiBinder.class);
    private static final StandardServiceAsync STANDARD_SERVICE = GWT.create(StandardService.class);

    interface StandardReportDialogUiBinder extends UiBinder<Widget, StandardReportDialog> {
    }

    @UiField
    MAFormPanel formPanel;
    @UiField
    Button saveButton;
    @UiField
    Button saveAsButton;
    @UiField
    ScrollPanel scrollPanel;
    @UiField
    Button runButton;

    ReportItemDTO report;

    private Main mainPanel;

    public StandardReportDialog() {
        add(uiBinder.createAndBindUi(this));
    }

    /**
     * Load the standard report parameter prompts.
     *
     * @param report
     */
    public void load(ReportItemDTO report) {
        if (report == null) {
            return;
        }
        this.report = report;
        formPanel.showLoading();
        scrollPanel.setSize("", "");
        formatSize();


        // Query and load the prompts in the background...
        boolean isInstance = (report.getReportInstanceId() != null ? true : false);
        saveButton.setVisible(isInstance);
        if (isInstance) {
            buildWidgets(report.getReportInstanceId(), formPanel, isInstance);
        } else {
            buildWidgets(report.getReportTemplateId(), formPanel, isInstance);
        }
    }

    public void setMainPanel(Main mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * Build screen widgets (labels, text boxes, combo boxes, etc) for a given report.
     * The same method is called for both Instances and Templates. isInstance governs that.
     *
     * @param reportId
     * @param form
     */
    private void buildWidgets(Long reportId, final MAFormPanel form, boolean isInstance) {


        if (STANDARD_SERVICE == null) {
            GWT.log("Standard Service is unavailable");
            return;
        }
        STANDARD_SERVICE.getStandardParamDTOList(reportId, isInstance,
            new AsyncCallback<List<StandardParamItemDTO>>() {
                public void onFailure(Throwable caught) {
                    GWT.log("Caught: " + caught.getClass());
                    GWT.log(caught.getMessage(), caught);
                    List<MAComponent> list = new ArrayList<MAComponent>();
                    form.setWidgets(list);
                    formatSize();
                    Window.alert(caught.getMessage());
                }

                public void onSuccess(List<StandardParamItemDTO> result) {
                	
                    List<MAComponent> list = new ArrayList<MAComponent>();
                    MAComponent parentComponent = null;
                    MAComponent component;
                    for (StandardParamItemDTO dto : result) {

                        if (dto.isSubParam() && dto.getParentName() == null) {
                            component = buildWidget(dto, parentComponent);
                        } else {
                            component = buildWidget(dto, null);
                        }

                        if (dto.isSubParam() && dto.getParentParam() == null) {
                            parentComponent = component;
                        }

                        list.add(component);
                    }
                    form.setWidgets(list);
                    formatSize();
                }
            });
    }

    /**
     * Build a single screen component based on a given StandardParamItemDTO.
     *
     * @param dto
     * @return
     */
    private MAComponent buildWidget(final StandardParamItemDTO dto, MAComponent parentComponent) {
        MAComponent comp;

        if (dto.getReportInstanceID() == null) {
            return buildWidgetForTemplate(dto, parentComponent);
        } else {
            return buildWidgetForInstance(dto, parentComponent);
        }
    }

    private MAComponent buildWidgetForInstance(final StandardParamItemDTO dto, MAComponent parentComponent) {
        MAComponent comp;
        switch (dto.getWizStyle()) {
            case CBO:    // list of Single Selects
                if (dto.isParamSQLBased()) {
                    final MASelector selector = new MASelector(parentComponent);
                    selector.setEnableSelectAll(dto.isParamSelAll());

                    if (("*").equals(dto.getDefaultParamCodeValueInCrystalReport()) && ((dto.getaValue() == null) || (("*").equalsIgnoreCase(dto.getaValue())))) {
                        selector.setAllSelected(true);
                    } else {
                        ParamAvailableItemValue val = new ParamAvailableItemValue();
                        val.setCode(dto.getaValue());
                        selector.setMAValue(val);
                    }

                    comp = selector;
                } else {
                    final MAListBox lb = new MAListBox();
                    if (dto.getDefaultParamCodeValueInCrystalReport().equals("*") && ((dto.getaValue() == null) || (dto.getaValue().equalsIgnoreCase("*")))) {
                        lb.addMAItem("*", "ALL");
                    } else {
                        /* lb.addMAItem(dto.getDefaultParamCodeValueInCrystalReport(),
                        dto.getDefaultParamDescriptionInCrystalReport());*/
                        lb.addMAItem(dto.getaValue(), "");
                    }
                    // lb.setSelectedIndex(0);
                    loadList(lb, dto, dto.getDefaultParamCodeValueInCrystalReport());

                    comp = lb;
                }
                break;
            case LIST: // List of Multiple Selects
                if (dto.isParamSQLBased()) {
                    final MASelector selector = new MASelector(true, parentComponent);

                    selector.setEnableSelectAll(dto.isParamSelAll());

                    if (dto.getDefaultParamCodeValueInCrystalReport().equals("*") && ((dto.getaValue() == null) || (dto.getaValue().equalsIgnoreCase("*")))) {
                        selector.setAllSelected(true);
                    } else {


                        List<ParamAvailableItemValue> storedValues = new ArrayList<ParamAvailableItemValue>();
                          dto.setaValueList(dto.getaValue());
                        for (String val : dto.getaValueList()) {
                            ParamAvailableItemValue pai = new ParamAvailableItemValue();
                            pai.setCode(val);
                            storedValues.add(pai);
                        }
                        selector.setMAValues(storedValues);
                    }

                    comp = selector;
                } else {
                    final MAListBox lbm = new MAListBox(true);
                    if (dto.getDefaultParamCodeValueInCrystalReport().equals("*")) {
                        lbm.addMAItem("*", "ALL");
                    } else {
                        lbm.addMAItem(dto.getDefaultParamCodeValueInCrystalReport(),
                            dto.getDefaultParamDescriptionInCrystalReport());
                    }
                    //   lbm.setSelectedIndex(0);

                    loadList(lbm, dto, dto.getDefaultParamCodeValueInCrystalReport());
                    comp = lbm;
                }
                break;
            case CALENDAR:
                comp = new MADateBox();
                comp.setMAValue(dto.getaValue());
                break;
            case CHK: // not being used in the application
                comp = new MACheckBox();
                comp.setMAValue(dto.getaValue());
                break;
            default:    // simple old text box!
                comp = new MATextBox();

                if (dto.getaValue().equals("*")) {
                    comp.setMAValue("ALL");
                } else {
                    comp.setMAValue(dto.getaValue());
                }

                break;
        }
        comp.setMALabel(dto.getPromptDescription());
        comp.setDTO(dto);

        return comp;
    }

    private MAComponent buildWidgetForTemplate(final StandardParamItemDTO dto, MAComponent parentComponent) {
        MAComponent comp;
       
        switch (dto.getWizStyle()) {
            case CBO:
                // db based values use the Selector
                if (dto.isParamSQLBased()) {
                    final MASelector lb = new MASelector(parentComponent);
                    lb.setEnableSelectAll(dto.isParamSelAll());
                    lb.setAllSelected(dto.isParamSelAll());
                    ParamAvailableItemValue val = new ParamAvailableItemValue();
                    val.setCode(dto.getDefaultParamCodeValueInCrystalReport());
                    val.setName(dto.getDefaultParamDescriptionInCrystalReport());
                    lb.setMAValue(val);
                    comp = lb;
                } else {
                    final MAListBox lb = new MAListBox();
                    if (dto.getDefaultParamCodeValueInCrystalReport().equals("*")) {
                        lb.addMAItem("*", "ALL");
                    } else {
                        lb.addMAItem(dto.getDefaultParamCodeValueInCrystalReport(),
                            dto.getDefaultParamDescriptionInCrystalReport());
                    }
                    loadList(lb, dto, dto.getDefaultParamCodeValueInCrystalReport());

                    comp = lb;
                }

                break;

            case LIST: // should be a list of check boxes!!

                if (dto.isParamSQLBased()) {
                    final MASelector lbm = new MASelector(true, parentComponent);

                    lbm.setEnableSelectAll(dto.isParamSelAll());

                    String def = dto.getDefaultParamCodeValueInCrystalReport();

                    // "foo".equals(null) is an NPE in GWT client side
                    if (def != null && def.equals("*")) {
                        lbm.setAllSelected(true);
                    } else {
                        ParamAvailableItemValue val = new ParamAvailableItemValue();
                        val.setCode(dto.getDefaultParamCodeValueInCrystalReport());
                        val.setName(dto.getDefaultParamDescriptionInCrystalReport());
                        GWT.log("---" + val.toString());

                        lbm.setMAValue(val);
                    }
                    comp = lbm;
                } else {
                    final MAListBox lb = new MAListBox();
                    if (dto.getDefaultParamCodeValueInCrystalReport().equals("*")) {
                        lb.addMAItem("*", "ALL");
                    } else {
                        lb.addMAItem(dto.getDefaultParamCodeValueInCrystalReport(),
                            dto.getDefaultParamDescriptionInCrystalReport());
                    }
                    loadList(lb, dto, dto.getDefaultParamCodeValueInCrystalReport());
                    comp = lb;
                }
                break;
            case CALENDAR:
                comp = new MADateBox();
                comp.setMAValue(dto.getDefaultParamCodeValueInCrystalReport());
                break;
            case CHK:
                comp = new MACheckBox();
                comp.setMAValue(dto.getDefaultParamCodeValueInCrystalReport());
                break;
            default:
                comp = new MATextBox();
                if (dto.getDefaultParamCodeValueInCrystalReport().equals("*")) {
                    comp.setMAValue("ALL");
                } else {
                    comp.setMAValue(dto.getDefaultParamCodeValueInCrystalReport());
                }

                break;
        }
        comp.setMALabel(dto.getPromptDescription());
        comp.setDTO(dto);
        return comp;
    }


    /**
     * Load the contents of a list in the background.
     *
     * @param lb
     * @param dto
     * @param defaultVal
     */
    private void loadList(final MAListBox lb, final StandardParamItemDTO dto,
                          final String defaultVal) {
        if (STANDARD_SERVICE == null) {
            GWT.log("Standard Service is unavailable");
            return;
        }
        STANDARD_SERVICE.getAvailableValuesForCBOorLIST(dto,
            new AsyncCallback<ParamAvailableItems>() {

                public void onFailure(Throwable caught) {
                    GWT.log("Caught: " + caught.getClass());
                    GWT.log(caught.getMessage(), caught);
                    Window.alert(caught.getMessage());
                }

                public void onSuccess(ParamAvailableItems result) {
                    if (lb.getItemCount() > 0) {
                        lb.clear();
                    }
                    if (dto.getReportInstanceID() != null) {
                        if (dto.getWizStyle().equals(StandardParamItemDTO.WizStyleEnum.LIST)) {
                            lb.setMAData(result.getParams(), dto.getaValueList());
                        } else {
                            lb.setMAData(result.getParams(), dto.getaValue());
                        }
                    } else {
                        lb.setMAData(result.getParams(), defaultVal);
                    }
                }
            });
    }

    public List<StandardParamItemDTO> getData() {
        List<StandardParamItemDTO> list = new ArrayList<StandardParamItemDTO>();
        List<MAComponent> comps = formPanel.getWidgets();
        for (MAComponent comp : comps) {
            StandardParamItemDTO dto = (StandardParamItemDTO) comp.getDTO();
            //inspect here if the dto is of type LIST, or CBO or TEXT or DATE
            switch (dto.getWizStyle()) {
                case LIST:
                    populateAValueForLIST(dto, comp);
                    break;

                case CALENDAR:
                    Date dateVal = (Date) comp.getMAValue();
                    DateTimeFormat formatClientDate = DateTimeFormat.getFormat("MM/dd/yyyy");
                    String mmddyyyy = "";
                    if (dateVal != null) {
                        mmddyyyy = formatClientDate.format(dateVal);
                    }
                    //Setting modified to true if found value has been changed
                    if (!mmddyyyy.equals(dto.getaValue())) {
                        dto.setModified(true);
                    }

                    dto.setaValue(mmddyyyy);
                    break;

                case CBO:
                    List<ParamAvailableItemValue> selectedValueList = (List) comp.getMAValue();
                    if (selectedValueList.size() == 1) {
                        ParamAvailableItemValue p = selectedValueList.get(0);
                        if (p.getCode() != null) {
                            //Setting modified to true if found value has been changed
                            if (!p.getCode().equalsIgnoreCase(dto.getaValue())) {
                                dto.setModified(true);
                            }
                            if (p.getCode().equals("*")) {
                                dto.setaValue("*");
                            } else {
                                dto.setaValue(p.getCode() == null ? "" : p.getCode());
                            }
                        }

                    } else {
                        if (!"".equals(dto.getaValue())) {
                            dto.setModified(true);
                        }
                        dto.setaValue("");
                    }
                    break;

                case TEXT:
                    //Setting modified to true if found value has been changed
                    if (!comp.getMAValue().toString().equalsIgnoreCase(dto.getaValue())) {
                        dto.setModified(true);
                    }
                    if (comp.getMAValue().equals("ALL")) {
                        dto.setaValue("*");
                    } else {
                        dto.setaValue(comp.getMAValue() == null ? "" : comp.getMAValue().toString());
                    }
                    break;

            }
            list.add((StandardParamItemDTO) comp.getDTO());
        }
        return list;
    }

    private void populateAValueForLIST(StandardParamItemDTO dto, MAComponent comp) {
        List<ParamAvailableItemValue> selectedList = (List) comp.getMAValue();
        StringBuilder returnList = new StringBuilder();
        String finalList;
        if (selectedList.size() > 0) {
            if (selectedList.get(0).getCode().equals("*")) {
                //Setting modified to true if found value has been changed
                if (!"*".equalsIgnoreCase(dto.getaValue())) {
                    dto.setModified(true);
                }
                dto.setaValue("*");
                return;
            }
            for (ParamAvailableItemValue s : selectedList) {

                returnList.append("\"" + s.getCode() + "\",");
            }
            finalList = returnList.substring(0, returnList.length() - 1);
            //Setting modified to true if found value has been changed
            if (!finalList.equalsIgnoreCase(dto.getaValue())) {
                dto.setModified(true);
            }
            dto.setaValue(finalList);
        } else {
            if (!"".equals(dto.getaValue())) {
                dto.setModified(true);
            }
            dto.setaValue("");
        }

    }

    /**
     * This method updated the standard report instance by calling the remote service.
     */
    public void updateReportInstance() {
        STANDARD_SERVICE.updateStandardReportInstance(getData(), buildSupportDTO(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Caught: " + caught.getClass());
                GWT.log(caught.getMessage(), caught);
                Window.alert("Unable to update the instance :" + caught.getMessage());
            }

            @Override
            public void onSuccess(Void aVoid) {
                //If instance is updated successfully hide the dialog.
                // StandardReportDialog.this.hide();
            }
        });
    }

    /**
     * This method creates a new instance of the template .
     */
    public void saveReportInstance() {
        String reportInstanceName = promptReportInstanceName();

        STANDARD_SERVICE.saveStandardReportInstance(reportInstanceName, getData(), buildSupportDTO(),
            new AsyncCallback<Void>() {

                public void onFailure(Throwable caught) {
                	
                    GWT.log("Caught: " + caught.getClass());
                    GWT.log(caught.getMessage(), caught);
                    Window.alert("Unable to save the instance: " + caught.getMessage());
              
                }

                public void onSuccess(Void v) {
                    //If instance is created successfully then hide the dialog.
                    
                	//StandardReportDialog.this.hide();
                	
                }
            });
    }

    @UiHandler("saveButton")
    void handleSaveClick(ClickEvent e) {
    	

        //Verify if its template or instance.
        boolean isInstance = (report.getReportInstanceId() != null ? true : false);

        /**
         * If save button is clicked on an instance then we need to perform update operation,
         * if save button is clicked on a template then save operation needs to be performed.
         */
        if (isInstance) {
            //Updating the instance.
            updateReportInstance();
        } else {
            //Creating a new instance of a template.
            saveReportInstance();
        }
    }


    private String promptReportInstanceName() {


        String reportName = Window.prompt("Please enter the report Name", report.getName());
        
        if (reportName.length() == 0) {
            Window.alert("Not a valid report name.  Please enter a name that is not blank.");
            promptReportInstanceName();
        }


        if (reportName.length() > 50) {
            Window.alert("The new report name you have entered is too long.  Please enter a name with 50 characters or less");
            promptReportInstanceName();
        }


        return reportName;


    }

    private StandardRptInstanceSupportItemDTO buildSupportDTO() {
        StandardRptInstanceSupportItemDTO dto = new StandardRptInstanceSupportItemDTO();
        dto.setFolderID(report.getFolderId());
        dto.setPublished('c');
        dto.setRptInstanceDescription(report.getDescription());
        dto.setRptInstanceName(report.getName());
        dto.setSubTitleName(report.getSubTitle());
        dto.setTemplateID(report.getReportTemplateId());
        //Setting the the instance id.
        dto.setInstanceID(report.getReportInstanceId());
        return dto;
    }


    @UiHandler("saveAsButton")
    void handleSaveAsClick(ClickEvent e) {

        saveReportInstance();
    }

    @Override
    public void onResize(ResizeEvent event) {
        //     if (this.isShowing()) {
        formatSize();
        //     }
    }

    @UiHandler("runButton")
    void handleRunClick(ClickEvent e) {
        GWT.log("Running report");
        List<StandardParamItemDTO> list = new ArrayList<StandardParamItemDTO>();

        /* for (MAComponent component : formPanel.getWidgets()) {
            StandardParamItemDTO dto = (StandardParamItemDTO) component.getDTO();
            list.add(dto);
        }*/

        // The report may take a while to open so lets just put a panel up saying so
        Label msg = new Label("Report Loading...");
        Panel msgPanel = new SimplePanel(msg);
        mainPanel.setMainPanel(msgPanel, "Loading Standard Report");

        list = getData();

        STANDARD_SERVICE.runStandardReport(report, list,
            new AsyncCallback<Void>() {
                public void onFailure(Throwable throwable) {
                    GWT.log("Caught: " + throwable.getClass());
                    GWT.log("Crystal Run failed");
                    Window.alert("Fail: " + throwable);

                }

                public void onSuccess(Void aVoid) {
                    GWT.log("Run succeeded. Session should contain crystal obj");
                    //openReport("Standard Report", "/StandardReportCrystalViewer.jsp");
                    openReport("Standard Report", "Test2.pdf");
                }
            });
    }

    /**
     * Don't allow the dialog to grow bigger than the window.
     */
    private void formatSize() {
        int w = (int) (Window.getClientWidth() * .85);
        int h = (int) (Window.getClientHeight() * .85);
        if (scrollPanel.getOffsetWidth() > w) {
            scrollPanel.setWidth(w + "px");
        }
        if (scrollPanel.getOffsetHeight() > h) {
            scrollPanel.setHeight(h + "px");
        }
    }

    public void openReport(String name, String url) {


        Frame frame = new Frame(url);
        Panel panel = new SimplePanel(frame);
        frame.setSize("100%", "100%");
        panel.setSize("100%", "100%");
        mainPanel.setMainPanel(panel, name);

    }

    /**
     * Opens a new windows with a specified URL..
     *
     * @param name String with the name of the window.
     * @param url  String with your URL.
     */
    public static void openNewWindow(String name, String url) {
        com.google.gwt.user.client.Window.open(url, name.replace(" ", "_"),
            "menubar=no," +
                "location=false," +
                "resizable=yes," +
                "scrollbars=yes," +
                "status=no," +
                "dependent=true");

        com.google.gwt.user.client.Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                closeSession();

            }


        });
    }

    private static void closeSession() {
        STANDARD_SERVICE.voidSessionCloseDocument(
            new AsyncCallback<Void>() {
                public void onFailure(Throwable throwable) {
                    GWT.log("Caught: " + throwable.getClass());
                    GWT.log("Closing session failed");
                    Window.alert("Fail: " + throwable);

                }

                public void onSuccess(Void aVoid) {
                    GWT.log("Able to remove RDC object from session");

                }
            });

    }

}
