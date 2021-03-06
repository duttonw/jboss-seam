package org.jboss.seam.ui.component;

import static org.jboss.seam.util.Strings.emptyIfNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.DataModel;

import org.jboss.seam.framework.Query;
import org.jboss.seam.ui.converter.ConverterChain;
import org.jboss.seam.ui.converter.NoSelectionConverter;
import org.jboss.seam.util.Strings;
import org.richfaces.cdk.annotations.Alias;
import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component which creates a List<SelectItem> from a List, Set, DataModel or Array.
 * 
 * @author Pete Muir
 *
 */
@JsfComponent(description = @Description(displayName = "org.jboss.seam.ui.SelectItems", value = "Creates a List<SelectItem> from a List, Set, DataModel or Array."), family = "javax.faces.SelectItems", type = "org.jboss.seam.ui.SelectItems", generate = "org.jboss.seam.ui.component.html.HtmlSelectItems", tag = @Tag(baseClass = "org.jboss.seam.ui.util.cdk.UIComponentTagBase", name = "selectItems"), attributes = {
		"base-props.xml", "javax.faces.component.UICommand.xml", "javax.faces.component.UIComponent.xml" })
public abstract class UISelectItems extends javax.faces.component.UISelectItems {

	private List<javax.faces.model.SelectItem> selectItems;
	private Object originalValue;

	private class NullableSelectItem extends javax.faces.model.SelectItem {

		private static final long serialVersionUID = 1L;
		private Object value;

		private NullableSelectItem(Object value, String label) {
			super.setLabel(label);
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

	}

	private abstract class ContextualSelectItem {

		private Object varValue;

		public ContextualSelectItem(Object varValue) {
			if (Strings.isEmpty(getVar())) {
				throw new FacesException("var attribute must be set in this s:selectItems");
			}
			if (varValue == null) {
				throw new FacesException("One item in the list to s:selectItems is null");
			}
			this.varValue = varValue;
		}

		/**
		 * @return the varValue
		 */
		protected Object getVarValue() {
			return this.varValue;
		}

		private void setup() {
			Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
			if (requestMap.containsKey(getVar())) {
				throw new FacesException("var with name:[" + getVar() + "] is already in use");
			}
			requestMap.put(getVar(), varValue);
		}

		private void cleanup() {
			getFacesContext().getExternalContext().getRequestMap().remove(getVar());
		}

		protected abstract Object getSelectItemValue();

		protected abstract String getSelectItemLabel();

		protected abstract boolean getSelectItemDisabled();

		protected abstract boolean getSelectItemEscape();

		protected javax.faces.model.SelectItem create() {
			try {
				setup();
				return new javax.faces.model.SelectItem(this.getSelectItemValue(), this.getSelectItemLabel(), "",
						this.getSelectItemDisabled(), this.getSelectItemEscape());
			} finally {
				cleanup();
			}
		}
	}

	private static final String NO_SELECTION_VALUE = null;

	/* Kinder impl of get/setLabel */

	private String label;

	@Attribute(aliases = {
			@Alias("itemLabel") }, description = @Description("the label to be used when rendering the SelectItem. Can reference the var variable"))
	public String getLabel() {
		ValueExpression ve = getValueExpression("label");
		if (ve != null) {
			Object object = ve.getValue(getFacesContext().getELContext());
			if (object != null) {
				return object.toString();
			}
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public abstract void setHideNoSelectionLabel(boolean hideNoSelectionLabel);

	@Attribute(defaultValue = "false", description = @Description("if true, the noSelectionLabel will be hidden when a value is selected"))
	public abstract boolean isHideNoSelectionLabel();

	@Attribute(description = @Description("specifies the (optional) label to place at the top of list "
			+ "(if required=\"true\" is also specified then selecting this value will cause a validation error)"))
	public abstract String getNoSelectionLabel();

	public abstract void setNoSelectionLabel(String noSelectionLabel);

	@Attribute(description = @Description("defines the name of the local variable that holds the current object during iteration"))
	public abstract String getVar();

	public abstract void setVar(String var);

	@Attribute(aliases = {
			@Alias("itemDisabled") }, description = @Description("if true the SelectItem will be rendered disabled. Can reference the var variable"))
	public abstract boolean isDisabled();

	public abstract void setDisabled(boolean disabled);

	@Attribute(defaultValue = "true", description = @Description("if false, characters in the label will not be escaped. "
			+ "Beware that this is a safety issue when the label is in any way derived from input supplied by the application's user. . Can reference the var variable"))
	public abstract boolean isEscape();

	public abstract void setEscape(boolean escape);

	@Attribute(description = @Description("Value to return to the server if this option is selected. "
			+ "Optional, by default the var object is used. Can reference the var variable"))
	public abstract Object getItemValue();

	public abstract void setItemValue(Object itemValue);

	@Override
	public void decode(FacesContext context) {
		super.decode(context);
		addConverterChainIfRequired();
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);
		addConverterChainIfRequired();
	}

	private void addConverterChainIfRequired() {
		if (isShowNoSelectionLabel()) {
			ConverterChain converterChain = new ConverterChain(this.getParent());
			Converter noSelectionConverter = new NoSelectionConverter();
			// Make sure that the converter is only added once
			if (!converterChain.containsConverterType(noSelectionConverter)) {
				converterChain.addConverterToChain(noSelectionConverter, ConverterChain.CHAIN_START);
			}
		}
	}

	@Override
	@Attribute(description = @Description("an EL expression specifying the data that backs the List&lt;SelectItem&gt;"))
	public Object getValue() {
		List<javax.faces.model.SelectItem> temporarySelectItems = new ArrayList<javax.faces.model.SelectItem>();
		javax.faces.model.SelectItem noSelectionLabel = noSelectionLabel();
		if (noSelectionLabel != null) {
			temporarySelectItems.add(noSelectionLabel);
		}
		if (selectItems == null || originalValue == null || !originalValue.equals(super.getValue())) {
			originalValue = super.getValue();
			selectItems = new ArrayList<javax.faces.model.SelectItem>();

			if (originalValue instanceof Iterable) {
				selectItems.addAll(asSelectItems((Iterable) originalValue));
			} else if (originalValue instanceof DataModel && ((DataModel) originalValue).getWrappedData() instanceof Iterable) {
				selectItems.addAll(asSelectItems((Iterable) ((DataModel) originalValue).getWrappedData()));
			} else if (originalValue instanceof Query) {
				selectItems.addAll(asSelectItems(((Query) originalValue).getResultList()));
			} else if (originalValue != null && originalValue.getClass().isArray()) {
				selectItems.addAll(asSelectItems(arrayAsList(originalValue)));
			} else if (temporarySelectItems.size() == 0) {
				return originalValue;
			}

		}
		temporarySelectItems.addAll(selectItems);
		return temporarySelectItems;
	}

	private List<javax.faces.model.SelectItem> asSelectItems(Iterable iterable) {
		List<javax.faces.model.SelectItem> selectItems = new ArrayList<javax.faces.model.SelectItem>();
		for (final Object o : iterable) {
			selectItems.add(o instanceof javax.faces.model.SelectItem ? (javax.faces.model.SelectItem) o : new ContextualSelectItem(o) {

				@Override
				protected boolean getSelectItemDisabled() {
					Boolean disabled = isDisabled();
					return disabled == null ? false : disabled;
				}

				@Override
				protected boolean getSelectItemEscape() {
					Boolean escape = isEscape();
					return escape == null ? true : escape;
				}

				@Override
				protected String getSelectItemLabel() {
					return emptyIfNull(getLabel());
				}

				@Override
				protected Object getSelectItemValue() {
					Object value = getItemValue();
					return value == null ? getVarValue() : value;
				}

			}.create());
		}
		return selectItems;
	}

	private javax.faces.model.SelectItem noSelectionLabel() {
		if (isShowNoSelectionLabel()) {
			NullableSelectItem s = new NullableSelectItem(NO_SELECTION_VALUE, getNoSelectionLabel());
			return s;
		} else {
			return null;
		}
	}

	private boolean isShowNoSelectionLabel() {
		ValueExpression vb = getValueExpression("noSelectionLabel");
		String noSelectionLabel = getNoSelectionLabel();
		boolean hideNoSelectionLabel = isHideNoSelectionLabel();
		Object parentValue = getParentValue();

		/*
		 * This is a slight hack. If you do an EL expresison like this (to hide the label)
		 * 
		 * noSelectionLabel="#{x eq y ? 'Please Select' : null}"
		 * 
		 * then, if x != y, EL will return an empty String, not null, so we work around that, with the side effect
		 * that if the result of the EL expression is an empty String, then the label will be hidden.
		 */
		if (noSelectionLabel != null && vb == null && !(hideNoSelectionLabel && parentValue != null)) {
			/* 
			 * Here, the user has specfied a noSelectionLabel (may be an empty string), and, if hideNoSelectionLabel
			 * is set, then, if a value is selected, then the label is hidden
			 */
			return true;
		} else if (noSelectionLabel != null && !"".equals(noSelectionLabel) && !(hideNoSelectionLabel && parentValue != null)) {
			/*
			 * Here, the user has used an EL expression as the noSelectionLabel.  In this case, an empty string is
			 * indicates that the label should be hidden.
			 */
			return true;
		} else {
			return false;
		}
	}

	private Object getParentValue() {
		if (getParent() instanceof ValueHolder) {
			ValueHolder parent = (ValueHolder) getParent();
			return parent.getValue();
		} else {
			return null;
		}
	}

	private static List arrayAsList(Object array) {
		if (array.getClass().getComponentType().isPrimitive()) {
			List list = new ArrayList();
			for (int i = 0; i < Array.getLength(array); i++) {
				list.add(Array.get(array, i));
			}
			return list;
		} else {
			return Arrays.asList((Object[]) array);
		}
	}
}
