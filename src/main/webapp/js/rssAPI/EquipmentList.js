export const getEquipmentList = (props) => {
    const { equipmentList } = props;
    return equipmentList.toJS();
};