// src/utils/imageHelper.js

// 图片服务器地址（开发环境）
const IMAGE_BASE_URL =
  import.meta.env.VITE_IMAGE_BASE_URL || "http://localhost:8080";

/**
 * 将图片URL转换为完整URL
 * @param {string} url 原始图片URL
 * @returns {string} 完整的图片URL
 */
export const fixImageUrl = (url) => {
  if (!url) return "";
  // base64 图片（如图形验证码），不需要拼接服务器地址
  if (url.startsWith("data:")) {
    return url;
  }
  // 已经是完整URL，直接返回
  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url;
  }
  // 相对路径（以/开头），拼接 base URL
  if (url.startsWith("/")) {
    return `${IMAGE_BASE_URL}${url}`;
  }
  // 如果只是文件名，补全路径
  return `${IMAGE_BASE_URL}/uploads/${url}`;
};

/**
 * 处理对象中的图片字段
 * @param {Object} obj 数据对象
 * @param {Array} fields 需要处理的图片字段名
 * @returns {Object} 处理后的对象
 */
export const fixImageFields = (
  obj,
  fields = ["coverImage", "avatar", "image", "imageUrl", "targetImage"],
) => {
  if (!obj || typeof obj !== "object") return obj;
  const result = { ...obj };
  fields.forEach((field) => {
    if (result[field]) {
      result[field] = fixImageUrl(result[field]);
    }
  });
  return result;
};

/**
 * 处理数组中的图片字段
 * @param {Array} arr 数据数组
 * @param {Array} fields 需要处理的图片字段名
 * @returns {Array} 处理后的数组
 */
export const fixImageArray = (
  arr,
  fields = ["coverImage", "avatar", "image", "imageUrl", "targetImage"],
) => {
  if (!Array.isArray(arr)) return arr;
  return arr.map((item) => fixImageFields(item, fields));
};

/**
 * 递归处理深层嵌套的数据
 * @param {any} data 数据
 * @returns {any} 处理后的数据
 */
export const fixImageDeep = (data) => {
  if (!data) return data;

  // 如果是数组，递归处理每个元素
  if (Array.isArray(data)) {
    return data.map((item) => fixImageDeep(item));
  }

  // 如果是对象，处理对象属性
  if (typeof data === "object") {
    const result = { ...data };
    // 处理常见的图片字段
    const imageFields = [
      "coverImage",
      "imageUrl",
      "targetImage",
      "avatar",
      "image",
      "cover",
      "thumbnail",
      "photo",
      "picture",
    ];
    imageFields.forEach((field) => {
      if (result[field] && typeof result[field] === "string") {
        result[field] = fixImageUrl(result[field]);
      }
    });
    // 递归处理嵌套对象
    Object.keys(result).forEach((key) => {
      if (result[key] && typeof result[key] === "object") {
        result[key] = fixImageDeep(result[key]);
      }
    });
    return result;
  }

  return data;
};
