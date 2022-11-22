package bridge.controller;

import bridge.service.BridgeGame;
import bridge.BridgeMaker;
import bridge.BridgeRandomNumberGenerator;
import bridge.domain.Bridge;
import bridge.domain.GameStatus;
import bridge.validator.BridgeLengthValidator;
import bridge.validator.RetryInputValidator;
import bridge.validator.SpaceToMoveValidator;
import bridge.view.InputView;
import bridge.view.OutputView;
import camp.nextstep.edu.missionutils.Console;

public class BridgeGameController {

    private static final String SUCCESS = "성공";
    private static final String RETRY = "R";
    private static final String QUIT = "Q";

    private final InputView inputView;
    private final OutputView outputView;
    private final BridgeMaker bridgeMaker;
    private final BridgeGame bridgeGame;
    private final GameStatus gameStatus;
    private Bridge bridge;
    private Bridge copyBridge;
    private boolean isFinish;

    public BridgeGameController() {
        inputView = new InputView();
        outputView = new OutputView();
        bridgeMaker = new BridgeMaker(new BridgeRandomNumberGenerator());
        bridgeGame = new BridgeGame();
        gameStatus = new GameStatus();
        isFinish = true;
    }

    public void start() {
        makeBridge(getBridgeLength());
        outputView.printStart();
        while (isFinish) {
            boolean check = crossTheBridge();
            outputView.printMap(gameStatus);
            askWhetherRetry(check);
            ifGoThroughTheBridge();
        }
        outputView.printResult(gameStatus);
    }

    private void ifGoThroughTheBridge() {
        if (copyBridge.getBridge().size() == 0) {
            gameStatus.gameResult = SUCCESS;
            isFinish = false;
        }
    }

    private void askWhetherRetry(boolean check) {
        String retry = askToRetryIfFailed(check);
        ifWantToQuitTheGame(retry);
    }

    private void ifWantToQuitTheGame(String retry) {
        if (retry.equals(QUIT)) {
            isFinish = false;
        }
    }

    private String askToRetryIfFailed(boolean check) {
        String retry = RETRY;
        if (!check) {
            retry = getWhetherToRetry();

            if (retry.equals(RETRY)) {
                bridgeGame.retry(gameStatus);
                copyBridge = new Bridge(bridge.copyBridge()); // Call by value, Call by reference
            }
        }
        return retry;
    }

    private boolean crossTheBridge() {
        String moving = getSpaceToMove();
        return bridgeGame.move(gameStatus, copyBridge, moving);
    }

    private void makeBridge(int bridgeLength) {
        bridge = new Bridge(bridgeMaker.makeBridge(bridgeLength));
        copyBridge = new Bridge(bridge.copyBridge());
    }

    private String getWhetherToRetry() {
        outputView.askWhetherToRetry();
        while (true) {
            try {
                String input = inputView.readGameCommand();
                RetryInputValidator.validateRetryInput(input);
                return input;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private String getSpaceToMove() {
        outputView.askSpaceToMove();
        while (true) {
            try {
                String input = inputView.readMoving();
                SpaceToMoveValidator.validateSpaceToMove(input);
                return input;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private int getBridgeLength() {
        outputView.askBridgeSize();
        while (true) {
            try {
                String input = Console.readLine();
                BridgeLengthValidator.validateNaturalNumber(input);
                BridgeLengthValidator.validateRange(input);
                return Integer.parseInt(input);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}