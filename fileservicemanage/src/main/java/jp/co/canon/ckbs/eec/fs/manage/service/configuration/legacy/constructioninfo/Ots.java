package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.constructioninfo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Ots {
    @Getter @Setter
    String name;

    @Getter @Setter
    String host;

    @Getter @Setter
    String version;

    Map<String, Equipment> equipmentMap = new HashMap<>();

    public void addEquipment(Equipment equipment){
        equipmentMap.put(equipment.getName(), equipment);
    }
}
