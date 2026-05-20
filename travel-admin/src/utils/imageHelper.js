const IMAGE_BASE_URL = import.meta.env.VITE_IMAGE_BASE_URL || "";

export const fixImageUrl = (url) => {
  if (!url || typeof url !== "string") return url || "";
  if (url.startsWith("data:") || url.startsWith("blob:")) return url;
  if (url.startsWith("http://") || url.startsWith("https://")) return url;
  if (url.startsWith("/")) return `${IMAGE_BASE_URL}${url}`;
  return `${IMAGE_BASE_URL}/uploads/${url}`;
};

export const fixImageDeep = (data) => {
  if (!data) return data;
  if (Array.isArray(data)) return data.map((item) => fixImageDeep(item));
  if (typeof data !== "object") return data;

  const result = { ...data };
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
    if (typeof result[field] === "string") {
      result[field] = fixImageUrl(result[field]);
    }
  });

  Object.keys(result).forEach((key) => {
    if (result[key] && typeof result[key] === "object") {
      result[key] = fixImageDeep(result[key]);
    }
  });

  return result;
};
