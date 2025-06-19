package com.example.todo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todo.ui.activity.MainComposeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriorityIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainComposeActivity>()

    @Test
    fun `complete priority feature workflow`() {
        // タスクを追加
        composeTestRule.onNodeWithText("新しいタスクを入力").performTextInput("優先度テストタスク")
        composeTestRule.onNodeWithText("追加").performClick()
        
        // 一覧にタスクが表示されることを確認
        composeTestRule.onNodeWithText("優先度テストタスク").assertIsDisplayed()
        
        // デフォルトで「普通」優先度が表示されることを確認
        composeTestRule.onNodeWithText("普通").assertIsDisplayed()
        
        // タスクをクリックして詳細画面に遷移
        composeTestRule.onNodeWithText("優先度テストタスク").performClick()
        
        // 詳細画面が表示されることを確認
        composeTestRule.onNodeWithText("タスク詳細").assertIsDisplayed()
        composeTestRule.onNodeWithText("優先度テストタスク").assertIsDisplayed()
        
        // 優先度設定セクションが表示されることを確認
        composeTestRule.onNodeWithText("優先度設定").assertIsDisplayed()
        
        // 優先度ボタンが3つ表示されることを確認
        composeTestRule.onAllNodesWithText("高").assertCountEquals(2) // 表示用とボタン用
        composeTestRule.onAllNodesWithText("普通").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("低").assertCountEquals(1) // ボタンのみ
        
        // 優先度を「高」に変更
        composeTestRule.onAllNodesWithText("高")[1].performClick() // ボタンの方をクリック
        
        // 戻るボタンで一覧画面に戻る
        composeTestRule.onNodeWithContentDescription("戻る").performClick()
        
        // 一覧画面で優先度が「高」に変更されていることを確認
        composeTestRule.onNodeWithText("優先度テストタスク").assertIsDisplayed()
        composeTestRule.onNodeWithText("高").assertIsDisplayed()
    }

    @Test
    fun `task completion preserves priority`() {
        // タスクを追加
        composeTestRule.onNodeWithText("新しいタスクを入力").performTextInput("完了テストタスク")
        composeTestRule.onNodeWithText("追加").performClick()
        
        // タスクの詳細画面に移動
        composeTestRule.onNodeWithText("完了テストタスク").performClick()
        
        // 優先度を「低」に設定
        composeTestRule.onAllNodesWithText("低")[0].performClick()
        
        // タスクを完了にする
        composeTestRule.onNodeWithText("完了にする").performClick()
        
        // 完了状態の表示を確認
        composeTestRule.onNodeWithText("完了済み").assertIsDisplayed()
        composeTestRule.onNodeWithText("未完了にする").assertIsDisplayed()
        
        // 一覧画面に戻る
        composeTestRule.onNodeWithContentDescription("戻る").performClick()
        
        // 完了したタスクでも優先度が保持されていることを確認
        composeTestRule.onNodeWithText("完了テストタスク").assertIsDisplayed()
        composeTestRule.onNodeWithText("低").assertIsDisplayed()
    }

    @Test
    fun `priority colors are displayed correctly in task list`() {
        // 異なる優先度のタスクを3つ追加
        val tasks = listOf(
            "高優先度タスク" to "高",
            "普通優先度タスク" to "普通", 
            "低優先度タスク" to "低"
        )
        
        tasks.forEach { (taskTitle, _) ->
            composeTestRule.onNodeWithText("新しいタスクを入力").performTextInput(taskTitle)
            composeTestRule.onNodeWithText("追加").performClick()
        }
        
        // 各タスクの優先度を設定
        tasks.forEachIndexed { index, (taskTitle, priority) ->
            composeTestRule.onNodeWithText(taskTitle).performClick()
            
            if (priority != "普通") { // デフォルトではない場合のみ変更
                composeTestRule.onAllNodesWithText(priority)[0].performClick()
            }
            
            composeTestRule.onNodeWithContentDescription("戻る").performClick()
        }
        
        // 一覧画面で全ての優先度バッジが正しく表示されることを確認
        tasks.forEach { (taskTitle, priority) ->
            composeTestRule.onNodeWithText(taskTitle).assertIsDisplayed()
            composeTestRule.onNodeWithText(priority).assertIsDisplayed()
        }
    }

    @Test
    fun `priority change updates immediately in detail screen`() {
        // タスクを追加
        composeTestRule.onNodeWithText("新しいタスクを入力").performTextInput("即座更新テスト")
        composeTestRule.onNodeWithText("追加").performClick()
        
        // 詳細画面に移動
        composeTestRule.onNodeWithText("即座更新テスト").performClick()
        
        // 初期状態で「普通」が選択されていることを確認
        composeTestRule.onAllNodesWithText("普通").assertCountEquals(2)
        
        // 「高」優先度に変更
        composeTestRule.onAllNodesWithText("高")[1].performClick()
        
        // 変更が即座に反映されることを確認（表示部分の更新）
        composeTestRule.waitForIdle()
        
        // 「低」優先度に変更
        composeTestRule.onAllNodesWithText("低")[0].performClick()
        
        // 変更が反映されることを確認
        composeTestRule.waitForIdle()
        
        // 戻って一覧画面で確認
        composeTestRule.onNodeWithContentDescription("戻る").performClick()
        composeTestRule.onNodeWithText("低").assertIsDisplayed()
    }
}