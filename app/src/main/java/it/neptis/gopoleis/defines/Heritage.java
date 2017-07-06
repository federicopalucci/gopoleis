package it.neptis.gopoleis.defines;

import java.util.List;

public class Heritage {

    private int code;
    private String name;
    private String description;
    // TODO implement Heritage.image
    private String latitude;
    private String longitude;
    private String province;
    private String region;
    private String historicalPeriod;
    private String structureType;
    private String operator;

    private boolean visited;

    private List<Treasure> treasures;

    public Heritage(int code, String name, String description, String latitude, String longitude, String province, String historicalPeriod, String structureType, String operator, List<Treasure> treasures) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.historicalPeriod = historicalPeriod;
        this.structureType = structureType;
        this.operator = operator;
        this.treasures = treasures;
    }

    public Heritage(int code, String name, String description, String latitude, String longitude, List<Treasure> treasures) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.treasures = treasures;
    }

    public Heritage(int code, String name, String description, String latitude, String longitude) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Heritage(int code, String name, String description, String latitude, String longitude, String province, String region, String historicalPeriod, String structureType, boolean visited) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.region = region;
        this.historicalPeriod = historicalPeriod;
        this.structureType = structureType;
        this.visited = visited;
    }

    public Heritage() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHistoricalPeriod() {
        return historicalPeriod;
    }

    public void setHistoricalPeriod(String historicalPeriod) {
        this.historicalPeriod = historicalPeriod;
    }

    public String getStructureType() {
        return structureType;
    }

    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

}