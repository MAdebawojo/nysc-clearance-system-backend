package com.madebawojo.nysc.ppa.clearance.service.impl.clearance;

import com.madebawojo.nysc.ppa.clearance.util.ClearanceLetterVariables;
import com.madebawojo.nysc.ppa.clearance.util.DateFormatUtil;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class ClearanceTemplateContextBuilder {

    public Context build(ClearanceLetterVariables variables) {
        String formattedLetterDate = DateFormatUtil.formatDateToReadableString(variables.getClearanceLetterDate());

        Context context = new Context();
        context.setVariable("clearanceLetterDate", formattedLetterDate);
        context.setVariable("corpsMemberName", variables.getCorpsMemberName());
        context.setVariable("stateCode", variables.getStateCode());
        context.setVariable("callUpNumber", variables.getCallUpNumber());
        context.setVariable("clearanceMonthUpper", variables.getClearanceMonthUpper());
        context.setVariable("signatoryName", variables.getSignatoryName());
        context.setVariable("signatoryTitle", variables.getSignatoryTitle());

        return context;
    }
}

