package com.view.beans.filmStripApp;

import com.sbm.selfServices.view.utils.JSFUtils;

import com.sbm.selfServices.view.utils.PersonInfo;

import com.view.beans.filmStrip.SessionState;
import com.view.beans.filmStrip.UtilsBean;
import com.view.data.ItemNode;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.controller.TaskFlowId;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.fragment.RichRegion;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

public class FilmStripBean {
    private UtilsBean _utils = new UtilsBean();
    private RichRegion dynamicRegionBinding;

    public void handleFilmStripCardClick(ClientEvent clientEvent) {
        _utils.setEL("#{sessionScope.selectedItemId}", clientEvent.getParameters().get("itemNodeId"));
        String ItemNodeId = clientEvent.getParameters().get("itemNodeId").toString();
        if(ItemNodeId.equals("Local")){
            _utils.setEL("#{sessionScope.tripType}", "Local");
        }
        else if(ItemNodeId.equals("International")){
            _utils.setEL("#{sessionScope.tripType}", "Inter");
        }
        else if(ItemNodeId.equals("Training")){
            _utils.setEL("#{sessionScope.tripType}", "Training");
        }
        else if(ItemNodeId.equals("Event")){
            _utils.setEL("#{sessionScope.tripType}", "Event");
        }
        else if(ItemNodeId.equals("Expense")){
            _utils.setEL("#{sessionScope.tripType}", "Expense");
        }
//        if (ItemNodeId.equalsIgnoreCase("Setup")) {
//                    _utils.refreshView();
//        }
        dynamicRegionBinding.refresh(FacesContext.getCurrentInstance()); 
    } 

    public TaskFlowId getDynamicTaskFlowId() {
        TaskFlowId taskFlowId =
            new TaskFlowId("/WEB-INF/RequestsFlows/Business-Trip-Task-Flow.xml", "Business-Trip-Task-Flow");
        String itemId = null;
        String groupId = null;
		PersonInfo personInfo =
                        (PersonInfo)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{PersonInfo}");
        if (ADFContext.getCurrent().getSessionScope().get("selectedGroupId") != null)
            groupId = (String) ADFContext.getCurrent().getSessionScope().get("selectedGroupId");
        if (ADFContext.getCurrent().getSessionScope().get("selectedItemId") != null)
            itemId = (String) ADFContext.getCurrent().getSessionScope().get("selectedItemId");
        //
        SessionState sessionState = (SessionState) _utils.getSessionScope().get("SessionState");
		DCIteratorBinding iter =
                        com.sbm.selfServices.view.utils.ADFUtils.findIterator("PendingForApprovalROVO1Iterator");
                    ViewObject vo = iter.getViewObject();
                    vo.setNamedWhereClauseParam("assignee_number", personInfo.getPerosnNumber());
                    vo.setNamedWhereClauseParam("assignee_position", personInfo.getAssignee());
                    vo.setNamedWhereClauseParam("p_assignee_name", personInfo.getPersonName());
                    vo.executeQuery();
                    sessionState.parseRootMenu();
        ItemNode node = sessionState.getClusterMap().get(itemId);
        if (node != null) {
            String destUrl = node.getDestinationUrl();
            String result = destUrl.substring(0, destUrl.lastIndexOf("."));
            String localTFId = result.substring(destUrl.lastIndexOf("/") + 1);
            taskFlowId = new TaskFlowId(destUrl, localTFId);
        }
        return taskFlowId;
    } //getDynamicTaskFlowId

    public Map getFilmStripLayout() {
        return new HashMap<String, String>() {
            public String get(Object key) {
                try {
                    String groupId = null;
                    String itemId = "";
                    if (ADFContext.getCurrent().getSessionScope().get("selectedGroupId") != null)
                        groupId = (String) ADFContext.getCurrent().getSessionScope().get("selectedGroupId");
                    if (ADFContext.getCurrent().getSessionScope().get("selectedItemId") != null)
                        itemId = (String) ADFContext.getCurrent().getSessionScope().get("selectedItemId");
                    //
                    SessionState sessionState = (SessionState) _utils.getSessionScope().get("SessionState");
                    String rootMenuData = sessionState.fetchGridNodes(groupId);
                    FacesContext fctx = FacesContext.getCurrentInstance();
                    ExtendedRenderKitService erks = Service.getRenderKitService(fctx, ExtendedRenderKitService.class);
                    String js =
                        "filmStripLayoutManager.handleFilmDocumentLoad('" + rootMenuData + "','" + itemId + "');";
                    erks.addScript(fctx, js);
                } catch (Exception e) {
                    e.printStackTrace();
                } //try-catch
                return null;
            } //get
        };
    } //getFilmStripLayout

    public void toggleStripRender(ActionEvent actionEvent) {
        boolean hideStrip = (Boolean) _utils.evaluateEL("#{sessionScope.hideStrip}");
        if (hideStrip)
            _utils.setEL("#{sessionScope.hideStrip}", false);
        else
            _utils.setEL("#{sessionScope.hideStrip}", true);
        _utils.refreshView();
    } //toggleStripRender

    public void setDynamicRegionBinding(RichRegion dynamicRegionBinding) {
        this.dynamicRegionBinding = dynamicRegionBinding;
    }

    public RichRegion getDynamicRegionBinding() {
        return dynamicRegionBinding;
    }
    public static void showPopupMessage(String message){
        
          JSFUtils.setExpressionValue("#{sessionScope.templateErrorMessage}",
                                    message);
        ExtendedRenderKitService erkService =
            Service.getService(FacesContext.getCurrentInstance().getRenderKit(),
                               ExtendedRenderKitService.class);
        erkService.addScript(FacesContext.getCurrentInstance(),
                             "var hints = {autodismissNever:true}; " +
                             "AdfPage.PAGE.findComponent('templatep1').show(hints);");
           }
//        RichPopup myPopu1p =
//            (RichPopup)JSFUtils.findComponentInRoot("templatep1");
//        RichPopup.PopupHints ph = new RichPopup.PopupHints();
//        myPopu1p.show(ph);

    }

