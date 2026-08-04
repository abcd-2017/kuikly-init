import Foundation
import SwiftUI
import shared

struct KuiklyRenderViewPage : UIViewControllerRepresentable {
    var pageName: String
    var data: Dictionary<String, Any>
  //  typealiaUIViewControllerType = UINavigationController
    func makeUIViewController(context: Context) -> UINavigationController {
        // 初始化 Koin 容器与公共任务
        SetupIOSKoinKt.setupIOSKoin()
        let hrVC = KuiklyRenderViewController(pageName: pageName, pageData: data)
        return UINavigationController.init(rootViewController: hrVC)
    }

    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {

    }

    func dealloc() {

    }

}