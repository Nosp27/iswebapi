# iswebapi

![](https://github.com/Nosp27/iswebapi/workflows/Java%20CI/badge.svg)

Web api for app designed to support investors

---
Entities:
- Facility
  - Name
  - Description
  - RegionId
  - Coordinates
- Region
  - RegionId
  - RegionName
- Category
  - CatName

---
Relations
- {Facility <<-> Region}
- {Facility <<->> Category}
