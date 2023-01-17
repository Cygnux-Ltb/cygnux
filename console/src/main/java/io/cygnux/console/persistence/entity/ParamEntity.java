package io.cygnux.console.entity;

import io.cygnux.console.dao.constant.CommonQueryColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * StrategyParam Entity
 *
 * @author yellow013
 */
@Data
@Entity
@Table(name = "cyg_param")
public final class ParamEntity {

    @Id
    @Column(name ="uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;

    @Column(name =CommonQueryColumn.STRATEGY_ID)
    private int strategyId;

    @Column(name =CommonQueryColumn.STRATEGY_NAME)
    private String strategyName;

    @Column(name ="owner_type")
    private String ownerType;

    @Column(name ="owner")
    private String owner;

    @Column(name ="param_name")
    private String paramName;

    @Column(name ="param_type")
    private String paramType;

    @Column(name ="param_value")
    private String paramValue;

}
