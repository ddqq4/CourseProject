package models;

import java.time.LocalDate;

public class Contract {
    private int contractId;
    private int clientId;
    private int agentId;
    private Integer typeId;
    private LocalDate contractDate;
    private double amount;
    private double tariffRate;
    private Double insurancePayment;
    private InsuranceType insuranceType;

    public int getContractId() {
        return contractId;
    }
    public void setContractId(int contractId) {
        this.contractId = contractId;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public int getAgentId() {
        return agentId;
    }
    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public LocalDate getContractDate() {
        return contractDate;
    }
    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getTariffRate() {
        return tariffRate;
    }
    public void setTariffRate(double tariffRate) {
        this.tariffRate = tariffRate;
    }
    public Double getInsurancePayment() {
        return insurancePayment;
    }
    public void setInsurancePayment(Double insurancePayment) {
        this.insurancePayment = insurancePayment;
    }
    public InsuranceType getInsuranceType() {
        return insuranceType;
    }
    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
        this.typeId = (insuranceType != null) ? insuranceType.getTypeId() : null;
    }
}