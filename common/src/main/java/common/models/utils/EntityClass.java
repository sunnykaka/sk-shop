package common.models.utils;

/**
 * 
 * @author liubin
 *
 * @param <IDClass>
 */
public interface EntityClass<IDClass extends java.io.Serializable> {

	public IDClass getId();

    public void setId(IDClass id);
}