# Slimefun 模组的非官方移植版

## 描述

这是 Slimefun 模组的非官方 NeoForge 移植版，为 Minecraft 1.21.1+ 添加了一套科技与魔法物品系统。Slimefun 是一个开源模组，允许玩家建造并使用多方块机器来制作高级物品。

本模组目前处于早期开发阶段，且作者是初学者，请大家多多包涵，感谢理解。

##特性

· 科技物品：太阳能板、电机等
· 魔法物品：魔法书、附魔工具等
· 自动化系统：机器与管道
· 兼容 NeoForge 加载器

##安装

1. 确保已安装 Minecraft 1.21.1 或更高版本。
2. 下载并安装 [NeoForge](https://neoforged.net/)。
3. 下载本模组的 JAR 文件。
4. 将 JAR 文件放入 .minecraft/mods 文件夹。
5. 启动游戏。

###依赖项

· NeoForge 47.1.0+
· Minecraft 1.21.1+

##使用说明

启动游戏后，即可在创造模式或生存模式下使用 Slimefun 物品。更多信息请查阅游戏内指南或 Wiki。

##构建

###环境要求

· Java 21+
· Gradle 8.0+

###构建步骤

1. 克隆仓库：
   ```
   git clone https://github.com/AlerCello86767/neoforgemod_slimefun.git
   cd neoforgemod_slimefun
   ```
2. 构建模组：
   ```
   ./gradlew build
   ```
3. 输出文件位于 build/libs/ 目录下。

##参与贡献

欢迎贡献代码！请通过 Issues 或 Pull Requests 参与。

1. 复刻（Fork）本项目。
2. 创建一个特性分支。
3. 提交你的更改。
4. 推送并创建 Pull Request。

##许可证

本项目基于 MIT 的许可证，详见 [LICENSE](LICENSE) 文件。

##常见问题

· 模组无法加载？ 请检查 NeoForge 版本兼容性。
· 物品不显示？ 确保你在正确的维度/世界中使用了它们。

##联系方式

· GitHub Issues：[提交问题](https://github.com/AlerCello86767/neoforgemod_slimefun/issues)