package models;

public class InsuranceType {
    private int typeId;
    private String typeName;
    private double agentPercent;

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public double getAgentPercent() { return agentPercent; }
    public void setAgentPercent(double agentPercent) { this.agentPercent = agentPercent; }
}
