package com.cmc.zenefitserver.domain.policy.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "youthPolicyList")
@Getter
@Setter
@ToString
public class YouthPolicyList {

    @XmlElement(name = "youthPolicy")
    private YouthPolicy[] youthPolicies;
}
