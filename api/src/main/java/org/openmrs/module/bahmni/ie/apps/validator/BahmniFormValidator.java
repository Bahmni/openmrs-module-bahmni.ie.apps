package org.openmrs.module.bahmni.ie.apps.validator;

import org.openmrs.Form;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

@Handler(supports = { Form.class }, order = 100)
public class BahmniFormValidator implements Validator {

	@Override
	public boolean supports(Class klass) {
		return klass.equals(Form.class);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Form form = (Form) obj;
		List<Form> allForms = Context.getFormService().getAllForms(false);
		List<Form> filteredForms = allForms.parallelStream()
				.filter(f -> {
					boolean sameVersion = f.getVersion().equals(form.getVersion());
					boolean sameName = f.getName().equalsIgnoreCase(form.getName());
					boolean hasId = form.getId() != null;
					return sameName && sameVersion && !hasId;
				}).collect(Collectors.toList());
		if (!filteredForms.isEmpty()) {
			errors.reject("Form with same name and version already exists");
		}
	}
}
