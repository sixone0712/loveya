package jp.co.canon.cks.eec.fs.rssportal.model.plans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
public class RSSPlanCollectionPlan {
    private int planId;
    private String planType;
    private int ownerId;
    private String planName;
    private List<String> fabNames;
    private List<String> machineNames;
    private List<String> categoryCodes;
    private List<String> categoryNames;
    private List<String> commands;
    private String type;
    private String interval;
    private String description;
    private String start;
    private String from;
    private String to;
    private String lastCollection;
    private String status;
    private String detailedStatus;

    public RSSPlanCollectionPlan() {
        this.fabNames = new ArrayList<String>();
        this.machineNames = new ArrayList<String>();
        this.categoryCodes = new ArrayList<String>();
        this.categoryNames = new ArrayList<String>();
        this.commands = new ArrayList<String>();
    }

    public void setFabNames(String fabNames) {
        if(fabNames != null && fabNames.length() > 0) {
            String[] splitStr = fabNames.split(",");
            this.fabNames.addAll(Arrays.asList(splitStr));
        }
    }

    public void setMachineNames(String machineNames) {
        if(machineNames != null && machineNames.length() > 0) {
            String[] splitStr = machineNames.split(",");
            this.machineNames.addAll(Arrays.asList(splitStr));
        }
    }

    public void setCategoryCodes(String categoryCodes) {
        if(categoryCodes != null && categoryCodes.length() > 0) {
            String[] splitStr = categoryCodes.split(",");
            this.categoryCodes.addAll(Arrays.asList(splitStr));
        }
    }

    public void setCategoryNames(String categoryNames) {
        if(categoryNames != null && categoryNames.length() > 0) {
            String[] splitStr = categoryNames.split(",");
            this.categoryNames.addAll(Arrays.asList(splitStr));
        }
    }
    public void setCommands(String commands) {
        if(commands != null && commands.length() > 0) {
            String[] splitStr = commands.split(",");
            this.commands.addAll(Arrays.asList(splitStr));
        }
    }
}
