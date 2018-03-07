### 渲染功能模块
openGL渲染

####集成说明
将.jar或者.aar文件copy到app项目的lib目录中

在app的build.gradle中配置:

 1.新增：
 repositories {

        flatDir {

            dirs 'libs'

        }
    }
2.新增依赖

dependencies {

compile(name: 'srpaas_render_v0.1.0', ext: 'aar')

}

