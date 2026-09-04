package com.paton.domain.Vo;

public class BookVo {
    private Integer bookId;  //书籍id
    private String bookName; //书名
    private String bookAuthor;//作者
    private String bookPublish;//出版社
    private String isExist;  //是否可借
    private String coverImage; // 新增：图书封面路径
    private String description; // 新增：图书描述
    private Double bookPrice; // 新增：图书价格
    private String bookIntroduction; // 新增：图书简介
    private Integer stock;          // 总库存
    private Integer availableCount; // 剩余可借数量

    public Integer getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookPublish() {
        return bookPublish;
    }

    public String getIsExist() {
        return isExist;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(Double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public void setBookPublish(String bookPublish) {
        this.bookPublish = bookPublish;
    }

    public void setIsExist(String isExist) {
        this.isExist = isExist;
    }

    // 获取封面图片URL（处理默认封面）
    public String getCoverImageUrl() {
        if (coverImage != null && !coverImage.trim().isEmpty()) {
            return coverImage;
        }
        return "/images/default-book-cover.png";
    }

    // 获取简短的描述（用于列表显示）
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "暂无描述";
        }
        if (description.length() > 50) {
            return description.substring(0, 50) + "...";
        }
        return description;
    }

    // 获取简短的简介（用于列表显示）
    public String getShortIntroduction() {
        if (bookIntroduction == null || bookIntroduction.trim().isEmpty()) {
            return "暂无简介";
        }
        if (bookIntroduction.length() > 30) {
            return bookIntroduction.substring(0, 30) + "...";
        }
        return bookIntroduction;
    }
}