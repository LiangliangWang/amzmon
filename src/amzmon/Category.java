package amzmon;
import java.util.ArrayList;

public class Category {
	
	private String CategoryName;
	private Category ParentCategory;
	private ArrayList<Category> SubCategories = new ArrayList<Category>();
	
	public Category(String name){
		CategoryName = name;
	}
	
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}

	public Category getParentCategory() {
		return ParentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		ParentCategory = parentCategory;
	}
	
	public void addSubCategory(Category subCategory){
		SubCategories.add(subCategory);
		subCategory.setParentCategory(this);
	}
	
}
