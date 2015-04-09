package common.models.utils;

import java.io.Serializable;

/**
 * 
 * @author liubin
 *
 * @param <IDClass>
 */
public interface EntityClass<IDClass extends java.io.Serializable> extends Serializable {

	public IDClass getId();

    public void setId(IDClass id);
}