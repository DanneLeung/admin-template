@FilterDefs({
    @FilterDef(name = "tenantFilter", defaultCondition = "company_id=:tenantId", parameters = {@ParamDef(name = "tenantId", type = LongJavaType.class)})
})
package com.xcesys.template.admin.entity;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;
import org.hibernate.type.descriptor.java.LongJavaType;

