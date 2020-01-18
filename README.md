# iswebapi

![](https://github.com/Nosp27/iswebapi/workflows/Maven/badge.svg)

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
