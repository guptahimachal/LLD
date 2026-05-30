package LowLevelDesign.DesignSplitwise.Expense;

import LowLevelDesign.DesignSplitwise.BalanceSheetController;
import LowLevelDesign.DesignSplitwise.Expense.Split.ExpenseSplit;
import LowLevelDesign.DesignSplitwise.Expense.Split.Split;
import LowLevelDesign.DesignSplitwise.User.User;
import LowLevelDesign.DesignSplitwise.UserExpenseBalanceSheet;

import java.util.List;

public class ExpenseController {

    BalanceSheetController balanceSheetController;
    public ExpenseController(){
        balanceSheetController = new BalanceSheetController();
    }

    public Expense createExpense(String expenseId, String description, double expenseAmount,
                                 List<Split> splitDetails, ExpenseSplitType splitType, User paidByUser){

        ExpenseSplit expenseSplit = SplitFactory.getSplitObject(splitType);
        expenseSplit.validateSplitRequest(splitDetails, expenseAmount);

        Expense expense = new Expense(expenseId, expenseAmount, description, paidByUser, splitType, splitDetails);

        balanceSheetController.updateUserExpenseBalanceSheet(paidByUser, splitDetails, expenseAmount);

        return expense;
    }

    public void settleUp(User paidByUser, User paidToUser, Double amount) {

        UserExpenseBalanceSheet paidByUserExpenseBalanceSheet = paidByUser.getUserExpenseBalanceSheet();

//        validations

        if (!paidByUserExpenseBalanceSheet.getUserVsBalance().containsKey(paidToUser) || paidByUserExpenseBalanceSheet.getUserVsBalance().get(paidToUser.getUserId()).getAmountOwe() <= 0 ) {
            System.out.println(String.format("user - %d and %d are already settled up", paidByUser.getUserId(), paidToUser.getUserId()));
            return;
        }

        double actualAmountOwe = paidByUserExpenseBalanceSheet.getUserVsBalance().get(paidToUser.getUserId()).getAmountOwe();

        if (actualAmountOwe != amount) {
            System.out.println(String.format("Total amount and amount differ bt %d", actualAmountOwe - amount));
        }


        balanceSheetController.settleUp(paidByUser, paidToUser, amount);

    }

}
