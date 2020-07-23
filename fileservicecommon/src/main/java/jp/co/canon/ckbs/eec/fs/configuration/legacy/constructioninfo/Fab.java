package jp.co.canon.ckbs.eec.fs.configuration.legacy.constructioninfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fab {
    @Getter @Setter
    String name;

    Map<String, Equipment> equipmentMap = new HashMap<>();
    List<Equipment> equipmentList = new ArrayList<>();

    public void addMachine(Equipment equipment){
        equipmentMap.put(equipment.getName(), equipment);
        equipmentList.add(equipment);
    }

    public Equipment[] getMachineList(){
        return equipmentList.toArray(new Equipment[0]);
    }
}
